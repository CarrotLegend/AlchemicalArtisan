package com.carrot123.alchemicalartisan.client.screen;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.menu.AlloyFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class AlloyFurnaceScreen extends AbstractContainerScreen<AlloyFurnaceMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            AlchemicalArtisan.MOD_ID,
            "textures/gui/alloy_furnace.png"
    );

    private static final int ARROW_U = 0;
    private static final int ARROW_V = 167;
    private static final int ARROW_X = 26;
    private static final int ARROW_Y = 31;
    private static final int ARROW_WIDTH = 101;
    private static final int ARROW_HEIGHT = 19;

    private static final int FIRE_U = 102;
    private static final int FIRE_V = 167;
    private static final int FIRE_X = 50;
    private static final int FIRE_Y = 43;
    private static final int FIRE_WIDTH = 13;
    private static final int FIRE_HEIGHT = 13;

    public AlloyFurnaceScreen(AlloyFurnaceMenu menu, Inventory inventory, Component title) {
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

        graphics.blit(TEXTURE, x, y, 0, 0, 176, 166, 256, 256);

        int progress = Mth.clamp(
                this.menu.getCraftingProgress(ARROW_WIDTH),
                0,
                ARROW_WIDTH
        );

        if (progress > 0) {
            graphics.blit(
                    TEXTURE,
                    x + ARROW_X,
                    y + ARROW_Y,
                    ARROW_U,
                    ARROW_V,
                    progress,
                    ARROW_HEIGHT,
                    256,
                    256
            );
        }

        if (this.menu.isBurning()) {
            int height = Mth.clamp(
                    Mth.ceil(this.menu.getFuelProgress() * FIRE_HEIGHT / 15.0F),
                    0,
                    FIRE_HEIGHT
            );

            if (height > 0) {
                graphics.blit(
                        TEXTURE,
                        x + FIRE_X,
                        y + FIRE_Y + FIRE_HEIGHT - height,
                        FIRE_U,
                        FIRE_V + FIRE_HEIGHT - height,
                        FIRE_WIDTH,
                        height,
                        256,
                        256
                );
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 7, 5, 0x59320D, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
