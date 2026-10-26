
package com.carrot123.alchemicalartisan.client.renderer;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.item.MortarItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MortarItemGeoModel extends GeoModel<MortarItem> {
    private static final ResourceLocation MODEL = new ResourceLocation(AlchemicalArtisan.MOD_ID, "geo/mortar.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlchemicalArtisan.MOD_ID, "textures/block/mortar.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(AlchemicalArtisan.MOD_ID, "animations/mortar.animation.json");

    @Override
    public ResourceLocation getModelResource(MortarItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MortarItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MortarItem animatable) {
        return ANIMATION;
    }
}
