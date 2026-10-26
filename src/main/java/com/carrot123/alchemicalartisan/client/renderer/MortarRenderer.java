package com.carrot123.alchemicalartisan.client.renderer;

import com.carrot123.alchemicalartisan.blockentity.MortarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MortarRenderer implements BlockEntityRenderer<MortarBlockEntity> {
    private final GeoBlockRenderer<MortarBlockEntity> geoRenderer;
    private final ItemRenderer itemRenderer;

    public MortarRenderer(BlockEntityRendererProvider.Context context) {
        geoRenderer = new GeoBlockRenderer<>(new MortarGeoModel());
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(MortarBlockEntity mortar, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        geoRenderer.render(mortar, partialTick, pose, buffer, packedLight, packedOverlay);

        ItemStack display = mortar.getDisplayStack();
        if (display.isEmpty()) {
            return;
        }

        pose.pushPose();
        pose.translate(0.5D, 0.245D, 0.5D);
        pose.mulPose(Axis.XP.rotationDegrees(90.0F));
        pose.scale(0.34F, 0.34F, 0.34F);
        itemRenderer.renderStatic(display, ItemDisplayContext.FIXED,
                packedLight, packedOverlay, pose, buffer, mortar.getLevel(), 0);
        pose.popPose();
    }
}
