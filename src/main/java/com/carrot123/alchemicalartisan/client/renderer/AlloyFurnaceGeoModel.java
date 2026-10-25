
package com.carrot123.alchemicalartisan.client.renderer;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.block.AlloyFurnaceBlock;
import com.carrot123.alchemicalartisan.blockentity.GeoAlloyFurnaceBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AlloyFurnaceGeoModel extends GeoModel<GeoAlloyFurnaceBlockEntity> {
    private static final ResourceLocation OFF_MODEL = new ResourceLocation(AlchemicalArtisan.MOD_ID, "geo/alloy_furnace_off.geo.json");
    private static final ResourceLocation ON_MODEL = new ResourceLocation(AlchemicalArtisan.MOD_ID, "geo/alloy_furnace_on.geo.json");
    private static final ResourceLocation OFF_TEXTURE = new ResourceLocation(AlchemicalArtisan.MOD_ID, "textures/block/alloy_furnace_off.png");
    private static final ResourceLocation ON_TEXTURE = new ResourceLocation(AlchemicalArtisan.MOD_ID, "textures/block/alloy_furnace_on.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(AlchemicalArtisan.MOD_ID, "animations/furnace.animation.json");

    @Override
    public ResourceLocation getModelResource(GeoAlloyFurnaceBlockEntity animatable) {
        return animatable.getBlockState().getValue(AlloyFurnaceBlock.LIT) ? ON_MODEL : OFF_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(GeoAlloyFurnaceBlockEntity animatable) {
        return animatable.getBlockState().getValue(AlloyFurnaceBlock.LIT) ? ON_TEXTURE : OFF_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAlloyFurnaceBlockEntity animatable) {
        return ANIMATION;
    }
}
