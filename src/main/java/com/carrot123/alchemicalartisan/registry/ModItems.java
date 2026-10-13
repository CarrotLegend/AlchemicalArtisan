package com.carrot123.alchemicalartisan.registry;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.item.ForgingHammerItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings ("null")
public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AlchemicalArtisan.MOD_ID);

    public static final RegistryObject<Item> PAPER_FORGING_BLUEPRINT = ITEMS.register("paper_forging_blueprint", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PARCHMENT_FORGING_BLUEPRINT = ITEMS.register("parchment_forging_blueprint", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HERBAL_FIBER = ITEMS.register("herbal_fiber", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANIMAL_SINEW = ITEMS.register("animal_sinew", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCESSED_SINEW = ITEMS.register("processed_sinew", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRUDE_ADHESIVE = ITEMS.register("crude_adhesive", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ADHESIVE = ITEMS.register("adhesive", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNIVERSAL_REPAIR_MATERIAL = ITEMS.register("universal_repair_material", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register("steel_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> WOODEN_FORGING_HAMMER = ITEMS.register("wooden_forging_hammer", () -> new ForgingHammerItem(Tiers.WOOD,2.0F, -2.8F, new Item.Properties().durability(Tiers.WOOD.getUses())));
    public static final RegistryObject<Item> IRON_FORGING_HAMMER = ITEMS.register("iron_forging_hammer", () -> new ForgingHammerItem(Tiers.IRON,2.0F, -2.8F, new Item.Properties().durability(Tiers.IRON.getUses())));
    public static final RegistryObject<Item> BRONZE_FORGING_HAMMER = ITEMS.register("bronze_forging_hammer", () -> new ForgingHammerItem(ModToolTiers.BRONZE,2.0F, -2.8F, new Item.Properties().durability(ModToolTiers.BRONZE.getUses())));
    public static final RegistryObject<Item> NETHERITE_FORGING_HAMMER = ITEMS.register("netherite_forging_hammer", () -> new ForgingHammerItem(Tiers.NETHERITE,2.0F, -2.8F, new Item.Properties().durability(Tiers.NETHERITE.getUses()).fireResistant()));
    public static final RegistryObject<Item> STEEL_FORGING_HAMMER = ITEMS.register("steel_forging_hammer", () -> new ForgingHammerItem(ModToolTiers.STEEL,2.0F, -2.8F, new Item.Properties().durability(ModToolTiers.STEEL.getUses()).fireResistant()));

    public static final RegistryObject<Item> BLAZING_FURNACE = ITEMS.register("blazing_furnace", () -> new BlockItem(ModBlocks.BLAZING_FURNACE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALLOY_FURNACE = ITEMS.register("alloy_furnace", () -> new BlockItem(ModBlocks.ALLOY_FURNACE.get(), new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}