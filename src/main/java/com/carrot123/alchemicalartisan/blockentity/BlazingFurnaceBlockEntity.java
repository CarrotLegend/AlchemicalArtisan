
package com.carrot123.alchemicalartisan.blockentity;

import com.carrot123.alchemicalartisan.menu.BlazingFurnaceMenu;
import com.carrot123.alchemicalartisan.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

@SuppressWarnings ("null")
public class BlazingFurnaceBlockEntity extends AbstractFurnaceBlockEntity {

    private static final int MAX_BATCH = 4;
    private static final float BONUS_CHANCE = 0.12F;

    public BlazingFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(
                ModBlockEntities.BLAZING_FURNACE.get(),
                pos,
                state,
                RecipeType.BLASTING
        );
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(
                "container.alchemicalartisan.blazing_furnace"
        );
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new BlazingFurnaceMenu(
            containerId,
            inventory,
            this,
            this.dataAccess
        );
    }

    @Override
    protected int getBurnDuration(ItemStack fuel) {
        int duration = super.getBurnDuration(fuel);
        return duration > 0 ? Math.max(1, duration / 2) : 0;
    }

    private int getBatchSize(ItemStack result) {
        ItemStack input = items.get(0);
        ItemStack existing = items.get(2);

        if (input.isEmpty() || result.isEmpty()) {
            return 0;
        }

        if (!existing.isEmpty()
                && !ItemStack.isSameItemSameTags(existing, result)) {
            return 0;
        }

        int maximum = Math.min(
                getMaxStackSize(),
                result.getMaxStackSize()
        );

        if (!existing.isEmpty()) {
            maximum = Math.min(maximum, existing.getMaxStackSize());
        }

        int available = maximum - existing.getCount();
        int perCraft = result.getCount();

        if (perCraft <= 0 || available < perCraft) {
            return 0;
        }

        return Math.min(
                Math.min(MAX_BATCH, input.getCount()),
                available / perCraft
        );
    }

    private void smeltBatch(
            Level level,
            BlastingRecipe recipe,
            ItemStack result,
            int batchSize
    ) {
        if (batchSize <= 0) {
            return;
        }

        ItemStack output = items.get(2);

        int amount = result.getCount() * batchSize;

        int maximum = Math.min(
                getMaxStackSize(),
                result.getMaxStackSize()
        );

        if (!output.isEmpty()) {
            maximum = Math.min(maximum, output.getMaxStackSize());
        }

        int currentCount = output.isEmpty() ? 0 : output.getCount();

        if (currentCount + amount + 1 <= maximum
                && level.random.nextFloat() < BONUS_CHANCE) {
            amount++;
        }

        if (output.isEmpty()) {
            ItemStack crafted = result.copy();
            crafted.setCount(amount);
            items.set(2, crafted);
        } else {
            output.grow(amount);
        }

        items.get(0).shrink(batchSize);

        for (int i = 0; i < batchSize; i++) {
            setRecipeUsed(recipe);
        }

        setChanged();
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            BlazingFurnaceBlockEntity furnace
    ) {
        int burnTime = furnace.dataAccess.get(0);
        int burnDuration = furnace.dataAccess.get(1);
        int progress = furnace.dataAccess.get(2);
        int totalTime = furnace.dataAccess.get(3);

        boolean wasLit = burnTime > 0;
        boolean changed = false;

        if (burnTime > 0) {
            burnTime--;
        }

        Optional<BlastingRecipe> recipeOptional =
            level.getRecipeManager().getRecipeFor(
                RecipeType.BLASTING,
                furnace,
                level
            );

        BlastingRecipe recipe = recipeOptional.orElse(null);

        ItemStack result = recipe == null
                ? ItemStack.EMPTY
                : recipe.assemble(
                        furnace,
                        level.registryAccess()
                );

        int batchSize = furnace.getBatchSize(result);

        if (recipe != null && batchSize > 0) {
            totalTime = Math.max(
                    1,
                    (recipe.getCookingTime() + 3) / 4
            );

            if (burnTime <= 0) {
                ItemStack fuel = furnace.items.get(1);

                if (!fuel.isEmpty()) {
                    int duration = furnace.getBurnDuration(fuel);

                    if (duration > 0) {
                        burnTime = duration;
                        burnDuration = duration;

                        ItemStack remainder =
                                fuel.getCraftingRemainingItem();

                        fuel.shrink(1);

                        if (fuel.isEmpty()) {
                            furnace.items.set(
                                    1,
                                    remainder
                            );
                        }

                        changed = true;
                    }
                }
            }

            if (burnTime > 0) {
                progress++;

                if (progress >= totalTime) {
                    progress = 0;

                    furnace.smeltBatch(
                            level,
                            recipe,
                            result,
                            batchSize
                    );

                    changed = true;
                }
            } else {
                progress = Math.max(0, progress - 2);
            }
        } else {
            progress = Math.max(0, progress - 2);
        }

        if (burnTime != furnace.dataAccess.get(0)
                || burnDuration != furnace.dataAccess.get(1)
                || progress != furnace.dataAccess.get(2)
                || totalTime != furnace.dataAccess.get(3)) {
            changed = true;
        }

        furnace.dataAccess.set(0, burnTime);
        furnace.dataAccess.set(1, burnDuration);
        furnace.dataAccess.set(2, progress);
        furnace.dataAccess.set(3, totalTime);

        boolean isLit = burnTime > 0;

        if (wasLit != isLit || state.getValue(AbstractFurnaceBlock.LIT) != isLit) {
            level.setBlock(
                    pos,
                    state.setValue(AbstractFurnaceBlock.LIT, isLit),
                    3
            );
            changed = true;
        }

        if (changed) {
            furnace.setChanged();
        }
    }
}
