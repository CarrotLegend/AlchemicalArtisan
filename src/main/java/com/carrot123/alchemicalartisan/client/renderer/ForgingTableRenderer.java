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

        pose.pushPose();
        pose.translate(0.5, 1.35, 0.5);
        pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        pose.mulPose(Axis.YP.rotationDegrees(180));

        pose.pushPose();
        pose.translate(0, 0.19, 0);
        pose.scale(0.38F, 0.38F, 0.38F);
        context.getItemRenderer().renderStatic(new ItemStack(icon), ItemDisplayContext.FIXED,
                light, overlay, pose, buffer, table.getLevel(), 0);
        pose.popPose();

        Component label = Component.translatable("forging.alchemicalartisan.part." + mode.getSerializedName());
        Font font = context.getFont();
        pose.scale(0.025F, -0.025F, 0.025F);
        font.drawInBatch(label, -font.width(label) / 2.0F, 0, 0xFFFFFF, false,
                pose.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
        pose.popPose();
    }
}
