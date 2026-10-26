package com.carrot123.alchemicalartisan.blockentity;

import com.carrot123.alchemicalartisan.block.MortarBlock;
import com.carrot123.alchemicalartisan.recipe.GrindingRecipe;
import com.carrot123.alchemicalartisan.registry.ModBlockEntities;
import com.carrot123.alchemicalartisan.registry.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;

public class MortarBlockEntity extends BlockEntity implements GeoBlockEntity {
    public static final int GRINDING_TICKS = 25;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.model.new");
    private static final RawAnimation GRIND = RawAnimation.begin().thenLoop("animation.model.new2");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ItemStack input = ItemStack.EMPTY;
    private ItemStack outputTemplate = ItemStack.EMPTY;
    private int remainingTicks;
    private int remainingOutput;

    public MortarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MORTAR.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "mortar", 0, state ->
                state.setAndContinue(getBlockState().getValue(MortarBlock.GRINDING) ? GRIND : IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1.0D);
    }

    public boolean canRetrieveInput() {
        return !input.isEmpty() && remainingTicks == 0;
    }

    public ItemStack getDisplayStack() {
        if (!input.isEmpty()) {
            return input.copy();
        }
        if (!outputTemplate.isEmpty() && remainingOutput > 0) {
            ItemStack display = outputTemplate.copy();
            display.setCount(Math.min(remainingOutput, display.getMaxStackSize()));
            return display;
        }
        return ItemStack.EMPTY;
    }

    public boolean interact(Player player, InteractionHand hand) {
        if (level == null || level.isClientSide) {
            return false;
        }

        if (remainingOutput > 0 && !outputTemplate.isEmpty()) {
            ItemStack extracted = outputTemplate.copy();
            int count = Math.min(remainingOutput, extracted.getMaxStackSize());
            extracted.setCount(count);
            remainingOutput -= count;
            if (remainingOutput == 0) {
                outputTemplate = ItemStack.EMPTY;
            }
            sync();
            player.getInventory().placeItemBackInInventory(extracted);
            return true;
        }

        if (remainingTicks > 0) {
            return true;
        }

        if (!input.isEmpty()) {
            if (player.isShiftKeyDown()) {
                ItemStack extracted = input.copy();
                reset();
                player.getInventory().placeItemBackInInventory(extracted);
            } else {
                remainingTicks = GRINDING_TICKS;
                setGrinding(true);
            }
            return true;
        }

        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return false;
        }

        Optional<GrindingRecipe> recipe = level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.GRINDING.get(), new SimpleContainer(held), level);
        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack result = recipe.get().getResultItem(level.registryAccess());
        if (result.isEmpty() || result.getCount() <= 0) {
            return false;
        }

        input = held.copy();
        outputTemplate = result.copy();
        remainingTicks = 0;
        remainingOutput = 0;
        if (!player.getAbilities().instabuild) {
            held.shrink(input.getCount());
        }
        sync();
        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MortarBlockEntity mortar) {
        if (mortar.remainingTicks <= 0) {
            return;
        }

        mortar.remainingTicks--;
        if (mortar.remainingTicks == 0) {
            if (!mortar.input.isEmpty() && !mortar.outputTemplate.isEmpty()) {
                mortar.remainingOutput = mortar.input.getCount() * mortar.outputTemplate.getCount();
            }
            mortar.input = ItemStack.EMPTY;
            mortar.setGrinding(false);
        } else {
            mortar.setChanged();
        }
    }

    private void reset() {
        input = ItemStack.EMPTY;
        outputTemplate = ItemStack.EMPTY;
        remainingTicks = 0;
        remainingOutput = 0;
        setGrinding(false);
    }

    private void setGrinding(boolean grinding) {
        if (level != null) {
            BlockState state = getBlockState();
            if (state.getValue(MortarBlock.GRINDING) != grinding) {
                level.setBlock(worldPosition, state.setValue(MortarBlock.GRINDING, grinding), 3);
            }
        }
        sync();
    }

    private void sync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void dropContents() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (!input.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D, input.copy());
        }
        if (remainingOutput > 0 && !outputTemplate.isEmpty()) {
            int count = remainingOutput;
            while (count > 0) {
                ItemStack extracted = outputTemplate.copy();
                int part = Math.min(count, extracted.getMaxStackSize());
                extracted.setCount(part);
                Containers.dropItemStack(level, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D,
                        worldPosition.getZ() + 0.5D, extracted);
                count -= part;
            }
        }
        input = ItemStack.EMPTY;
        outputTemplate = ItemStack.EMPTY;
        remainingTicks = 0;
        remainingOutput = 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!input.isEmpty()) {
            tag.put("Input", input.save(new CompoundTag()));
        }
        if (!outputTemplate.isEmpty()) {
            tag.put("OutputTemplate", outputTemplate.save(new CompoundTag()));
        }
        tag.putInt("RemainingTicks", remainingTicks);
        tag.putInt("RemainingOutput", remainingOutput);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        input = tag.contains("Input", 10) ? ItemStack.of(tag.getCompound("Input")) : ItemStack.EMPTY;
        outputTemplate = tag.contains("OutputTemplate", 10)
                ? ItemStack.of(tag.getCompound("OutputTemplate")) : ItemStack.EMPTY;
        remainingTicks = Math.max(0, tag.getInt("RemainingTicks"));
        remainingOutput = Math.max(0, tag.getInt("RemainingOutput"));
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
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            load(tag);
        }
    }
}
