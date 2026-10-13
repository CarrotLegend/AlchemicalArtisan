
package com.carrot123.alchemicalartisan.client.screen;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.menu.BlazingFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlazingFurnaceScreen extends AbstractContainerScreen<BlazingFurnaceMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            AlchemicalArtisan.MOD_ID,
            "textures/gui/blazing_furnace.png"
    );

    private static final int ARROW_WIDTH = 26;
    private static final int ARROW_HEIGHT = 21;
    private static final int FIRE_WIDTH = 13;
    private static final int FIRE_HEIGHT = 13;

    public BlazingFurnaceScreen(BlazingFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 7;
        this.titleLabelY = 5;
        this.inventoryLabelY = -100;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(
                TEXTURE,
                x, y,
                0, 0,
                176, 166,
                256, 256
        );

        if (this.menu.isBlazing()) {
            int height = this.menu.getBlazingFuelProgress(FIRE_HEIGHT);

            if (height > 0) {
                graphics.blit(
                        TEXTURE,
                        x + 57,
                        y + 38 + FIRE_HEIGHT - height,
                        28,
                        167 + FIRE_HEIGHT - height,
                        FIRE_WIDTH,
                        height,
                        256, 256
                );
            }
        }

        int progress = this.menu.getBlazingCookProgress(ARROW_WIDTH);

        if (progress > 0) {
            graphics.blit(
                    TEXTURE,
                    x + 78,
                    y + 30,
                    1,
                    167,
                    progress,
                    ARROW_HEIGHT,
                    256, 256
            );
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(
                this.font,
                this.title,
                this.titleLabelX,
                this.titleLabelY,
                0x404040,
                false
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
