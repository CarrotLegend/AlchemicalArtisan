package com.carrot123.alchemicalartisan.client.renderer;

import com.carrot123.alchemicalartisan.blockentity.ForgingTableBlockEntity;
import com.carrot123.alchemicalartisan.forging.ForgingToolType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ForgingTableRenderer implements BlockEntityRenderer<ForgingTableBlockEntity> {
    private final BlockEntityRendererProvider.Context context;

    public ForgingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(ForgingTableBlockEntity table, float partialTick, PoseStack pose,
            MultiBufferSource buffer, int light, int overlay) {
        ForgingToolType mode = table.getMode();

        if (mode == ForgingToolType.NONE) {
            return;
        }

        Item icon = switch (mode) {
            case SWORD -> Items.IRON_SWORD;
            case PICKAXE -> Items.IRON_PICKAXE;
            case AXE -> Items.IRON_AXE;
            case HOE -> Items.IRON_HOE;
            case SHOVEL -> Items.IRON_SHOVEL;
            case NONE -> Items.AIR;
        };

        ItemStack display = table.getMetal().isEmpty()
                ? new ItemStack(icon)
                : table.getMetal();

        if (!display.isEmpty()) {
            pose.pushPose();
            pose.translate(0.5D, 1.035D, 0.5D);
            pose.mulPose(Axis.XP.rotationDegrees(90.0F));
            pose.scale(0.65F, 0.65F, 0.65F);

            context.getItemRenderer().renderStatic(
                    display,
                    ItemDisplayContext.FIXED,
                    light,
                    overlay,
                    pose,
                    buffer,
                    table.getLevel(),
                    0
            );

            pose.popPose();
        }

        Component label = Component.translatable(
                "forging.alchemicalartisan.part." + mode.getSerializedName()
        );

        Font font = context.getFont();

        pose.pushPose();
        pose.translate(0.5D, 2.0D, 0.5D);
        pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        pose.scale(-0.025F, -0.025F, 0.025F);

        font.drawInBatch(
                label,
                -font.width(label) / 2.0F,
                0.0F,
                0xFFFFFF,
                false,
                pose.last().pose(),
                buffer,
                Font.DisplayMode.NORMAL,
                0,
                light
        );

        pose.popPose();
    }
}
