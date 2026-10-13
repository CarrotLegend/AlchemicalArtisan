package com.carrot123.alchemicalartisan.menu;

import com.carrot123.alchemicalartisan.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeType;

public class BlazingFurnaceMenu extends AbstractFurnaceMenu {

    public BlazingFurnaceMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(containerId, inventory);
    }

    public BlazingFurnaceMenu(int containerId, Inventory inventory) {
        super(ModMenuTypes.BLAZING_FURNACE.get(), RecipeType.BLASTING,
                RecipeBookType.BLAST_FURNACE, containerId, inventory);
    }

    public BlazingFurnaceMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenuTypes.BLAZING_FURNACE.get(), RecipeType.BLASTING,
                RecipeBookType.BLAST_FURNACE, containerId, inventory, container, data);
    }
}
