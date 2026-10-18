package com.carrot123.alchemicalartisan.registry;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

@SuppressWarnings ({"null", "removal"})
public class ModToolTiers {

    private static final TagKey<Item> BRONZE_INGOTS =
            TagKey.create(
                    Registries.ITEM,
                    new ResourceLocation("forge", "ingots/bronze")
            );

    private static final TagKey<Item> STEEL_INGOTS =
            TagKey.create(
                    Registries.ITEM,
                    new ResourceLocation("forge", "ingots/steel")
            );

    private static final TagKey<Item> ETHER_ALLOY_INGOTS =
            TagKey.create(
                    Registries.ITEM,
                    new ResourceLocation(
                            AlchemicalArtisan.MOD_ID,
                            "ingots/ether_alloy"
                    )
            );

    public static final Tier BRONZE =
            TierSortingRegistry.registerTier(
                    new ForgeTier(
                            3,
                            1561,
                            8.0F,
                            3.0F,
                            10,
                            Tiers.DIAMOND.getTag(),
                            () -> Ingredient.of(BRONZE_INGOTS)
                    ),
                    new ResourceLocation(
                            AlchemicalArtisan.MOD_ID,
                            "bronze"
                    ),
                    List.of(Tiers.DIAMOND),
                    List.of(Tiers.NETHERITE)
            );

    public static final Tier STEEL =
            TierSortingRegistry.registerTier(
                    new ForgeTier(
                            4,
                            2031,
                            9.0F,
                            4.0F,
                            15,
                            Tiers.NETHERITE.getTag(),
                            () -> Ingredient.of(STEEL_INGOTS)
                    ),
                    new ResourceLocation(
                            AlchemicalArtisan.MOD_ID,
                            "steel"
                    ),
                    List.of(Tiers.NETHERITE),
                    List.of()
            );

    public static final Tier ETHER_ALLOY =
            TierSortingRegistry.registerTier(
                    new ForgeTier(
                            5,
                            2500,
                            10.0F,
                            5.0F,
                            20,
                            Tiers.NETHERITE.getTag(),
                            () -> Ingredient.of(ETHER_ALLOY_INGOTS)
                    ),
                    new ResourceLocation(
                            AlchemicalArtisan.MOD_ID,
                            "ether_alloy"
                    ),
                    List.of(Tiers.NETHERITE, STEEL),
                    List.of()
            );

    public static void register() {
    }
}
