package com.carrot123.alchemicalartisan.client.renderer;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.blockentity.MortarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MortarGeoModel extends GeoModel<MortarBlockEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(AlchemicalArtisan.MOD_ID, "geo/mortar.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlchemicalArtisan.MOD_ID, "textures/block/mortar.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(AlchemicalArtisan.MOD_ID, "animations/mortar.animation.json");

    @Override
    public ResourceLocation getModelResource(MortarBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MortarBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MortarBlockEntity animatable) {
        return ANIMATION;
    }
}
