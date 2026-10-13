package com.carrot123.alchemicalartisan.client.screen;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.menu.AlloyFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AlloyFurnaceScreen extends AbstractContainerScreen<AlloyFurnaceMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            AlchemicalArtisan.MOD_ID, "textures/gui/alloy_furnace.png");
    private static final int PROGRESS_SOURCE_X = 2;
    private static final int PROGRESS_SOURCE_Y = 166;
    private static final int PROGRESS_WIDTH = 97;
    private static final int PROGRESS_HEIGHT = 14;
    private static final int FIRE_SOURCE_X = 100;
    private static final int FIRE_SOURCE_Y = 165;
    private static final int FIRE_WIDTH = 13;
    private static final int FIRE_HEIGHT = 15;

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 164;
        titleLabelX = 7;
        titleLabelY = -100;
        inventoryLabelY = -100;
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        int progress = menu.getCraftingProgress(PROGRESS_WIDTH);
        if (progress > 0) {
            graphics.blit(TEXTURE, x + 27, y + 35,
                    PROGRESS_SOURCE_X, PROGRESS_SOURCE_Y,
                    progress, PROGRESS_HEIGHT, 256, 256);
        }
        if (menu.isBurning()) {
            int height = menu.getFuelProgress();
            if (height > 0) {
                graphics.blit(TEXTURE, x + 49, y + 42 + (FIRE_HEIGHT - height),
                        FIRE_SOURCE_X, FIRE_SOURCE_Y + (FIRE_HEIGHT - height),
                        FIRE_WIDTH, height, 256, 256);
            }
        }
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }
}
