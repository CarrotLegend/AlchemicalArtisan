
package com.carrot123.alchemicalartisan.client.renderer;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.blockentity.GeoBlazingFurnaceBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import software.bernie.geckolib.model.GeoModel;

public class BlazingFurnaceGeoModel extends GeoModel<GeoBlazingFurnaceBlockEntity> {
    private static final ResourceLocation OFF_MODEL = new ResourceLocation(AlchemicalArtisan.MOD_ID, "geo/blazing_furnace_off.geo.json");
    private static final ResourceLocation ON_MODEL = new ResourceLocation(AlchemicalArtisan.MOD_ID, "geo/blazing_furnace_on.geo.json");
    private static final ResourceLocation OFF_TEXTURE = new ResourceLocation(AlchemicalArtisan.MOD_ID, "textures/block/blazing_furnace_off.png");
    private static final ResourceLocation ON_TEXTURE = new ResourceLocation(AlchemicalArtisan.MOD_ID, "textures/block/blazing_furnace_on.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(AlchemicalArtisan.MOD_ID, "animations/furnace.animation.json");

    @Override
    public ResourceLocation getModelResource(GeoBlazingFurnaceBlockEntity animatable) {
        return animatable.getBlockState().getValue(AbstractFurnaceBlock.LIT) ? ON_MODEL : OFF_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GeoBlazingFurnaceBlockEntity animatable) {
        return animatable.getBlockState().getValue(AbstractFurnaceBlock.LIT) ? ON_TEXTURE : OFF_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GeoBlazingFurnaceBlockEntity animatable) {
        return ANIMATION;
    }
}
