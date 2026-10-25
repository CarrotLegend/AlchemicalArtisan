package com.carrot123.alchemicalartisan.blockentity;

import com.carrot123.alchemicalartisan.block.ForgingTableBlock;
import com.carrot123.alchemicalartisan.forging.ForgingToolType;
import com.carrot123.alchemicalartisan.item.ForgingBlueprintItem;
import com.carrot123.alchemicalartisan.item.ForgingHammerItem;
import com.carrot123.alchemicalartisan.recipe.ForgingRecipe;
import com.carrot123.alchemicalartisan.registry.ModBlockEntities;
import com.carrot123.alchemicalartisan.registry.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class ForgingTableBlockEntity extends BlockEntity {
    private static final String BLUEPRINT_KEY = "Blueprint";
    private static final String METAL_KEY = "Metal";
    private static final String RECIPE_KEY = "Recipe";
    private static final String HAMMER_HITS_KEY = "HammerHits";

    private ItemStack blueprint = ItemStack.EMPTY;
    private ItemStack metal = ItemStack.EMPTY;
    private ResourceLocation recipeId;
    private int hammerHits;

    public ForgingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FORGING_TABLE.get(), pos, state);
    }

    public boolean hasBlueprint() {
        return !blueprint.isEmpty();
    }

    public ItemStack getMetal() {
        return metal;
    }

    public int getHammerHits() {
        return hammerHits;
    }

    public boolean installBlueprint(ItemStack stack) {
        if (hasBlueprint() || !(stack.getItem() instanceof ForgingBlueprintItem)
                || ForgingBlueprintItem.getToolType(stack) == ForgingToolType.NONE) {
            return false;
        }

        blueprint = stack.copy();
        blueprint.setCount(1);
        metal = ItemStack.EMPTY;
        recipeId = null;
        hammerHits = 0;
        syncState();
        return true;
    }

    public ItemStack takeBlueprint() {
        if (!metal.isEmpty() || hammerHits > 0) {
            return ItemStack.EMPTY;
        }

        ItemStack result = blueprint;
        blueprint = ItemStack.EMPTY;
        recipeId = null;
        hammerHits = 0;
        syncState();
        return result;
    }

    public ForgingToolType getMode() {
        return ForgingBlueprintItem.getToolType(blueprint);
    }

    public boolean interact(Player player, InteractionHand hand) {
        if (level == null || level.isClientSide) {
            return false;
        }

        ItemStack held = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!hasBlueprint()) {
                return false;
            }

            if (hammerHits > 0) {
                player.displayClientMessage(
                        Component.translatable("message.alchemicalartisan.forging_table.in_progress"),
                        true
                );
                return true;
            }

            if (!metal.isEmpty()) {
                giveToPlayer(player, metal);
                metal = ItemStack.EMPTY;
                recipeId = null;
                syncState();
                return true;
            }

            ItemStack returned = takeBlueprint();
            giveToPlayer(player, returned);
            return true;
        }

        if (!hasBlueprint()) {
            if (!(held.getItem() instanceof ForgingBlueprintItem)) {
                return false;
            }

            if (ForgingBlueprintItem.getToolType(held) == ForgingToolType.NONE) {
                player.displayClientMessage(
                        Component.translatable("message.alchemicalartisan.forging_table.need_typed_blueprint"),
                        true
                );
                return true;
            }

            ItemStack installed = held.copy();
            installed.setCount(1);

            if (installBlueprint(installed)) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                return true;
            }

            return false;
        }

        if (metal.isEmpty()) {
            if (held.isEmpty()) {
                return false;
            }

            if (held.getItem() instanceof ForgingHammerItem) {
                return true;
            }

            ForgingRecipe recipe = findRecipeForMetal(held);

            if (recipe == null) {
                player.displayClientMessage(
                        Component.translatable("message.alchemicalartisan.forging_table.no_matching_recipe"),
                        true
                );
                return true;
            }

            metal = held.copy();
            metal.setCount(recipe.getMetalCount());
            recipeId = recipe.getId();
            hammerHits = 0;

            if (!player.getAbilities().instabuild) {
                held.shrink(recipe.getMetalCount());
            }

            syncState();
            return true;
        }

        if (!(held.getItem() instanceof ForgingHammerItem)) {
            return false;
        }

        ForgingRecipe recipe = getCurrentRecipe();

        if (recipe == null) {
            player.displayClientMessage(
                    Component.translatable("message.alchemicalartisan.forging_table.no_matching_recipe"),
                    true
            );
            return true;
        }

        SimpleContainer input = new SimpleContainer(
                blueprint.copy(),
                metal.copy(),
                held.copy()
        );

        if (!recipe.matches(input, level)) {
            player.displayClientMessage(
                    Component.translatable("message.alchemicalartisan.forging_table.hammer_too_weak"),
                    true
            );
            return true;
        }

        hammerHits++;

        level.playSound(
                null,
                worldPosition,
                SoundEvents.ANVIL_USE,
                SoundSource.BLOCKS,
                0.6F,
                1.2F
        );

        if (!player.getAbilities().instabuild) {
            held.hurtAndBreak(
                    1,
                    player,
                    entity -> entity.broadcastBreakEvent(
                            hand == InteractionHand.MAIN_HAND
                                    ? EquipmentSlot.MAINHAND
                                    : EquipmentSlot.OFFHAND
                    )
            );
        }

        if (hammerHits >= recipe.getHitCount()) {
            ItemStack result = recipe.getResultItem(level.registryAccess()).copy();

            metal = ItemStack.EMPTY;
            recipeId = null;
            hammerHits = 0;

            consumeBlueprintUseAfterSuccessfulForge();
            giveToPlayer(player, result);
        } else {
            player.displayClientMessage(
                    Component.translatable(
                            "message.alchemicalartisan.forging_table.progress",
                            hammerHits,
                            recipe.getHitCount()
                    ),
                    true
            );
        }

        syncState();
        return true;
    }

    @Nullable
    private ForgingRecipe findRecipeForMetal(ItemStack held) {
        if (level == null || !hasBlueprint()) {
            return null;
        }

        ForgingToolType type = getMode();
        boolean paper = ((ForgingBlueprintItem) blueprint.getItem()).isPaper();

        for (ForgingRecipe recipe :
                level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.FORGING.get())) {
            if (recipe.getBlueprintType() != type) {
                continue;
            }

            if (recipe.getBlueprintMaterial() == ForgingRecipe.BlueprintMaterial.PAPER && !paper) {
                continue;
            }

            if (recipe.getBlueprintMaterial() == ForgingRecipe.BlueprintMaterial.PARCHMENT && paper) {
                continue;
            }

            if (!recipe.getHeatedMetal().test(held)
                    || held.getCount() < recipe.getMetalCount()) {
                continue;
            }

            return recipe;
        }

        return null;
    }

    @Nullable
    private ForgingRecipe getCurrentRecipe() {
        if (level == null || recipeId == null) {
            return null;
        }

        return level.getRecipeManager()
                .byKey(recipeId)
                .filter(recipe -> recipe instanceof ForgingRecipe)
                .map(recipe -> (ForgingRecipe) recipe)
                .orElse(null);
    }

    // 真实锻造成功后才扣除纸质蓝图耐久。
    public boolean consumeBlueprintUseAfterSuccessfulForge() {
        if (!(blueprint.getItem() instanceof ForgingBlueprintItem item)) {
            return false;
        }

        if (item.isPaper()) {
            int damage = blueprint.getDamageValue() + 1;

            if (damage >= blueprint.getMaxDamage()) {
                blueprint = ItemStack.EMPTY;
            } else {
                blueprint.setDamageValue(damage);
            }
        }

        syncState();
        return true;
    }

    private void giveToPlayer(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        ItemStack remaining = stack.copy();
        player.getInventory().add(remaining);

        if (!remaining.isEmpty()) {
            player.drop(remaining, false);
        }
    }

    private void syncState() {
        setChanged();

        if (level != null && !level.isClientSide) {
            BlockState state = level.getBlockState(worldPosition);
            ForgingToolType mode = getMode();

            if (state.hasProperty(ForgingTableBlock.MODE)
                    && state.getValue(ForgingTableBlock.MODE) != mode) {
                level.setBlock(
                        worldPosition,
                        state.setValue(ForgingTableBlock.MODE, mode),
                        3
                );
            }

            BlockState updated = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, updated, updated, 3);
        }
    }

    public void dropContents() {
        if (level == null || level.isClientSide) {
            return;
        }

        ItemStack droppedBlueprint = blueprint;
        ItemStack droppedMetal = metal;

        blueprint = ItemStack.EMPTY;
        metal = ItemStack.EMPTY;
        recipeId = null;
        hammerHits = 0;
        setChanged();

        if (!droppedBlueprint.isEmpty()) {
            Containers.dropItemStack(
                    level,
                    worldPosition.getX() + 0.5D,
                    worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D,
                    droppedBlueprint
            );
        }

        if (!droppedMetal.isEmpty()) {
            Containers.dropItemStack(
                    level,
                    worldPosition.getX() + 0.5D,
                    worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D,
                    droppedMetal
            );
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level != null && !level.isClientSide) {
            syncState();
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        blueprint = tag.contains(BLUEPRINT_KEY, 10)
                ? ItemStack.of(tag.getCompound(BLUEPRINT_KEY))
                : ItemStack.EMPTY;

        metal = tag.contains(METAL_KEY, 10)
                ? ItemStack.of(tag.getCompound(METAL_KEY))
                : ItemStack.EMPTY;

        recipeId = tag.contains(RECIPE_KEY, 8)
                ? ResourceLocation.tryParse(tag.getString(RECIPE_KEY))
                : null;

        hammerHits = Math.max(0, tag.getInt(HAMMER_HITS_KEY));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (!blueprint.isEmpty()) {
            tag.put(BLUEPRINT_KEY, blueprint.save(new CompoundTag()));
        }

        if (!metal.isEmpty()) {
            tag.put(METAL_KEY, metal.save(new CompoundTag()));
        }

        if (recipeId != null) {
            tag.putString(RECIPE_KEY, recipeId.toString());
        }

        tag.putInt(HAMMER_HITS_KEY, hammerHits);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();

        if (tag != null) {
            load(tag);
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1.0D, 2.0D, 1.0D);
    }
}
