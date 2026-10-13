package com.carrot123.alchemicalartisan.blockentity;

import com.carrot123.alchemicalartisan.block.AlloyFurnaceBlock;
import com.carrot123.alchemicalartisan.menu.AlloyFurnaceMenu;
import com.carrot123.alchemicalartisan.recipe.AlloyRecipe;
import com.carrot123.alchemicalartisan.registry.ModBlockEntities;
import com.carrot123.alchemicalartisan.registry.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class AlloyFurnaceBlockEntity extends BaseContainerBlockEntity {

    public static final int INPUT_COUNT = 4;
    public static final int FUEL_SLOT = 4;
    public static final int OUTPUT_SLOT = 5;
    public static final int SIZE = 6;

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int burnTime;
    private int burnDuration;
    private int progress;
    private int progressTotal = 200;
    private int burningFuelLevel;
    @Nullable private ResourceLocation currentRecipe;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> burnDuration;
                case 2 -> progress;
                case 3 -> progressTotal;
                case 4 -> burningFuelLevel;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> burnDuration = value;
                case 2 -> progress = value;
                case 3 -> progressTotal = value;
                case 4 -> burningFuelLevel = value;
            }
        }
        @Override public int getCount() { return 5; }
    };

    public AlloyFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALLOY_FURNACE.get(), pos, state);
    }

    public static int getFuelLevel(ItemStack stack) {
        if (stack.is(Items.CHARCOAL)) return 1;
        if (stack.is(Items.COAL) || stack.is(Items.COAL_BLOCK)) return 2;
        if (stack.is(Items.BLAZE_POWDER) || stack.is(Items.BLAZE_ROD)) return 3;
        return 0;
    }

    public static int getFuelDuration(ItemStack stack) {
        if (stack.is(Items.CHARCOAL) || stack.is(Items.COAL)) return 1600;
        if (stack.is(Items.COAL_BLOCK)) return 16000;
        if (stack.is(Items.BLAZE_POWDER)) return 1200;
        if (stack.is(Items.BLAZE_ROD)) return 2400;
        return 0;
    }

    @Override protected Component getDefaultName() {
        return Component.translatable("container.alchemicalartisan.alloy_furnace");
    }

    @Override protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new AlloyFurnaceMenu(id, inventory, this, data);
    }

    public ContainerData getData() { return data; }

    @Override public int getContainerSize() { return SIZE; }

    @Override public boolean isEmpty() {
        for (ItemStack stack : items) if (!stack.isEmpty()) return false;
        return true;
    }

    @Override public ItemStack getItem(int index) { return items.get(index); }

    @Override public ItemStack removeItem(int index, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, index, amount);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = ContainerHelper.takeItem(items, index);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override public void setItem(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == OUTPUT_SLOT) return false;
        return index != FUEL_SLOT || getFuelLevel(stack) > 0;
    }

    @Override public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override public void clearContent() {
        items.clear();
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        setChanged();
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
        burnTime = tag.getInt("BurnTime");
        burnDuration = tag.getInt("BurnDuration");
        progress = tag.getInt("Progress");
        progressTotal = Math.max(1, tag.getInt("ProgressTotal"));
        burningFuelLevel = tag.getInt("BurningFuelLevel");
        currentRecipe = tag.contains("CurrentRecipe") ? ResourceLocation.tryParse(tag.getString("CurrentRecipe")) : null;
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnDuration", burnDuration);
        tag.putInt("Progress", progress);
        tag.putInt("ProgressTotal", progressTotal);
        tag.putInt("BurningFuelLevel", burningFuelLevel);
        if (currentRecipe != null) tag.putString("CurrentRecipe", currentRecipe.toString());
    }

    private boolean canOutput(ItemStack result) {
        if (result.isEmpty()) return false;
        ItemStack output = items.get(OUTPUT_SLOT);
        if (output.isEmpty()) return result.getCount() <= Math.min(result.getMaxStackSize(), getMaxStackSize());
        return ItemStack.isSameItemSameTags(output, result)
                && output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), getMaxStackSize());
    }

    private void createResult(AlloyRecipe recipe, int[] assignment, ItemStack result) {
        recipe.consumeInputs(this, assignment);
        ItemStack output = items.get(OUTPUT_SLOT);
        if (output.isEmpty()) items.set(OUTPUT_SLOT, result.copy());
        else output.grow(result.getCount());
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AlloyFurnaceBlockEntity furnace) {
        boolean wasLit = furnace.burnTime > 0;
        if (furnace.burnTime > 0) furnace.burnTime--;
        if (furnace.burnTime == 0) furnace.burningFuelLevel = 0;

        SimpleContainer fourInputs = new SimpleContainer(INPUT_COUNT);
        for (int i = 0; i < INPUT_COUNT; i++) fourInputs.setItem(i, furnace.items.get(i).copy());
        AlloyRecipe recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.ALLOYING.get(), fourInputs, level)
                .orElse(null);

        int[] assignment = recipe == null ? null : recipe.findInputSlots(furnace);
        ItemStack result = recipe == null ? ItemStack.EMPTY : recipe.getResultItem(level.registryAccess());
        boolean ready = assignment != null && furnace.canOutput(result);

        if (!ready) {
            furnace.progress = 0;
            furnace.currentRecipe = null;
        } else {
            if (!recipe.getId().equals(furnace.currentRecipe)) {
                furnace.progress = 0;
                furnace.currentRecipe = recipe.getId();
            }
            furnace.progressTotal = Math.max(1, recipe.getCookingTime());
            if (furnace.burnTime <= 0) {
                ItemStack fuel = furnace.items.get(FUEL_SLOT);
                int levelRequired = recipe.getFuelLevel();
                int fuelLevel = getFuelLevel(fuel);
                int fuelDuration = getFuelDuration(fuel);
                if (fuelLevel >= levelRequired && fuelDuration > 0) {
                    furnace.burnTime = fuelDuration;
                    furnace.burnDuration = fuelDuration;
                    furnace.burningFuelLevel = fuelLevel;
                    fuel.shrink(1);
                }
            }
            if (furnace.burnTime > 0 && furnace.burningFuelLevel >= recipe.getFuelLevel()) {
                furnace.progress++;
                if (furnace.progress >= furnace.progressTotal) {
                    furnace.createResult(recipe, assignment, result);
                    furnace.progress = 0;
                }
            } else {
                furnace.progress = 0;
            }
        }

        boolean isLit = furnace.burnTime > 0;
        if (wasLit != isLit || state.getValue(AlloyFurnaceBlock.LIT) != isLit) {
            level.setBlock(pos, state.setValue(AlloyFurnaceBlock.LIT, isLit), 3);
        }
        furnace.setChanged();
    }
}
