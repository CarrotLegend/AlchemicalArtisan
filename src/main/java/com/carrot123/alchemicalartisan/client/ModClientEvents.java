package com.carrot123.alchemicalartisan.client;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.client.screen.AlloyFurnaceScreen;
import com.carrot123.alchemicalartisan.client.screen.BlazingFurnaceScreen;
import com.carrot123.alchemicalartisan.client.renderer.ForgingTableRenderer;
import com.carrot123.alchemicalartisan.registry.ModBlockEntities;
import com.carrot123.alchemicalartisan.registry.ModEntities;
import com.carrot123.alchemicalartisan.registry.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = AlchemicalArtisan.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(
                    ModMenuTypes.ALLOY_FURNACE.get(),
                    AlloyFurnaceScreen::new
            );

            MenuScreens.register(
                    ModMenuTypes.BLAZING_FURNACE.get(),
                    BlazingFurnaceScreen::new
            );
        });
    }

    @SubscribeEvent
    public static void registerEntityRenderers(
            EntityRenderersEvent.RegisterRenderers event
    ) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.FORGING_TABLE.get(),
                ForgingTableRenderer::new
        );
        event.registerEntityRenderer(
                ModEntities.EXPLOSIVE_PROJECTILE.get(),
                ThrownItemRenderer::new
        );
    }
}
