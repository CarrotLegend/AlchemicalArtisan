package com.carrot123.alchemicalartisan.menu;

import com.carrot123.alchemicalartisan.blockentity.AlloyFurnaceBlockEntity;
import com.carrot123.alchemicalartisan.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AlloyFurnaceMenu extends AbstractContainerMenu {

    private static final int PLAYER_START = 6;
    private static final int PLAYER_END = 42;
    private final Container container;
    private final ContainerData data;

    public AlloyFurnaceMenu(int id, Inventory inventory, FriendlyByteBuf ignored) {
        this(id, inventory, new SimpleContainer(AlloyFurnaceBlockEntity.SIZE), new SimpleContainerData(5));
    }

    public AlloyFurnaceMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(ModMenuTypes.ALLOY_FURNACE.get(), id);
        this.container = container;
        this.data = data;
        checkContainerSize(container, AlloyFurnaceBlockEntity.SIZE);
        checkContainerDataCount(data, 5);
        container.startOpen(inventory.player);

        for (int i = 0; i < 4; i++) {
            this.addSlot(new Slot(container, i, 21 + i * 18, 14));
        }
        this.addSlot(new Slot(container, AlloyFurnaceBlockEntity.FUEL_SLOT, 49, 59) {
            @Override public boolean mayPlace(ItemStack stack) {
                return AlloyFurnaceBlockEntity.getFuelLevel(stack) > 0;
            }
        });
        this.addSlot(new Slot(container, AlloyFurnaceBlockEntity.OUTPUT_SLOT, 135, 31) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + (row + 1) * 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
        addDataSlots(data);
    }

    public boolean isBurning() { return data.get(0) > 0; }
    public int getFuelLevel() { return data.get(4); }
    public int getFuelProgress() {
        int remaining = data.get(0);
        int total = data.get(1);
        return total <= 0 || remaining <= 0 ? 0 : Math.max(1, remaining * 15 / total);
    }
    public int getCraftingProgress(int maxPixels) {
        int current = data.get(2);
        int total = data.get(3);
        return total <= 0 || current <= 0 ? 0 : Math.min(maxPixels, current * maxPixels / total);
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return result;
        ItemStack stack = slot.getItem();
        result = stack.copy();

        if (index == AlloyFurnaceBlockEntity.OUTPUT_SLOT) {
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, result);
        } else if (index < PLAYER_START) {
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, false)) return ItemStack.EMPTY;
        } else {
            if (AlloyFurnaceBlockEntity.getFuelLevel(stack) > 0 && moveItemStackTo(stack, 4, 5, false)) {
            } else if (!moveItemStackTo(stack, 0, 4, false)) {
                if (index < 33) {
                    if (!moveItemStackTo(stack, 33, 42, false)) return ItemStack.EMPTY;
                } else if (!moveItemStackTo(stack, 6, 33, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return result;
    }

    @Override public boolean stillValid(Player player) { return container.stillValid(player); }

    @Override public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
