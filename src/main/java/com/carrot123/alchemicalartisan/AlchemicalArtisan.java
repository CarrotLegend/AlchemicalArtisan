package com.carrot123.alchemicalartisan;

import com.carrot123.alchemicalartisan.registry.ModBlockEntities;
import com.carrot123.alchemicalartisan.registry.ModBlocks;
import com.carrot123.alchemicalartisan.registry.ModCreativeTabs;
import com.carrot123.alchemicalartisan.registry.ModItems;
import com.carrot123.alchemicalartisan.registry.ModLootModifiers;
import com.carrot123.alchemicalartisan.registry.ModMenuTypes;
import com.carrot123.alchemicalartisan.registry.ModRecipeSerializers;
import com.carrot123.alchemicalartisan.registry.ModRecipeTypes;
import com.carrot123.alchemicalartisan.registry.ModToolTiers;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AlchemicalArtisan.MOD_ID)
@SuppressWarnings ("removal")
public class AlchemicalArtisan {
    public static final String MOD_ID = "alchemicalartisan";

    public AlchemicalArtisan() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModToolTiers.register();
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);
    }
}
