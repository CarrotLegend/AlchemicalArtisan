
package com.carrot123.alchemicalartisan.menu;

import com.carrot123.alchemicalartisan.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.util.Mth;

public class BlazingFurnaceMenu extends AbstractFurnaceMenu {

    private final ContainerData furnaceData;

    public BlazingFurnaceMenu(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(containerId, inventory);
    }

    public BlazingFurnaceMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    public BlazingFurnaceMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(
                ModMenuTypes.BLAZING_FURNACE.get(),
                RecipeType.BLASTING,
                RecipeBookType.BLAST_FURNACE,
                containerId,
                inventory,
                container,
                data
        );

        this.furnaceData = data;
    }

    public int getBlazingCookProgress(int width) {
        int progress = furnaceData.get(2);
        int total = furnaceData.get(3);

        if (progress <= 0 || total <= 0) {
            return 0;
        }

        return Mth.clamp(
                Mth.ceil((float) progress * width / total),
                0,
                width
        );
    }

    public int getBlazingFuelProgress(int height) {
        int remaining = furnaceData.get(0);
        int duration = furnaceData.get(1);

        if (remaining <= 0 || duration <= 0) {
            return 0;
        }

        return Mth.clamp(
                Mth.ceil((float) remaining * height / duration),
                0,
                height
        );
    }

    public boolean isBlazing() {
        return furnaceData.get(0) > 0;
    }
}
