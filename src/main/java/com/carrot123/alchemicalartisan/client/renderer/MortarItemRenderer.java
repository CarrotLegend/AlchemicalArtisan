package com.carrot123.alchemicalartisan.client.renderer;

import com.carrot123.alchemicalartisan.item.MortarItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MortarItemRenderer extends GeoItemRenderer<MortarItem> {
    public MortarItemRenderer() {
        super(new MortarItemGeoModel());
    }
}
