package com.carrot123.alchemicalartisan.registry;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.item.EtherExtractorItem;
import com.carrot123.alchemicalartisan.item.EtherFuelItem;
import com.carrot123.alchemicalartisan.item.ExplosiveItem;
import com.carrot123.alchemicalartisan.item.ForgingBlueprintItem;
import com.carrot123.alchemicalartisan.item.ForgingHammerItem;
import com.carrot123.alchemicalartisan.item.KnifeItem;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings ("null")
public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AlchemicalArtisan.MOD_ID);

    
    public static final RegistryObject<Item> PROCESSED_SHEEPSKIN = ITEMS.register("processed_sheepskin", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PAPER_FORGING_BLUEPRINT = ITEMS.register("paper_forging_blueprint", () -> new ForgingBlueprintItem(true, new Item.Properties().durability(3)));
    public static final RegistryObject<Item> PARCHMENT_FORGING_BLUEPRINT = ITEMS.register("parchment_forging_blueprint", () -> new ForgingBlueprintItem(false, new Item.Properties()));
    public static final RegistryObject<Item> HERBAL_FIBER = ITEMS.register("herbal_fiber", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANIMAL_SINEW = ITEMS.register("animal_sinew", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCESSED_SINEW = ITEMS.register("processed_sinew", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRUDE_ADHESIVE = ITEMS.register("crude_adhesive", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ADHESIVE = ITEMS.register("adhesive", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNIVERSAL_REPAIR_MATERIAL = ITEMS.register("universal_repair_material", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STEEL_INGOT = ITEMS.register("steel_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STEEL_NUGGET = ITEMS.register("steel_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_TIN = ITEMS.register("raw_tin", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIN_NUGGET = ITEMS.register("tin_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIN_DUST = ITEMS.register("tin_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BRONZE_INGOT = ITEMS.register("bronze_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BRONZE_NUGGET = ITEMS.register("bronze_nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BRONZE_DUST = ITEMS.register("bronze_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ETHER_EXTRACTOR = ITEMS.register("ether_extractor", () -> new EtherExtractorItem(new Item.Properties().stacksTo(1).durability(8)));
    public static final RegistryObject<Item> ETHER_FUEL = ITEMS.register("ether_fuel", () -> new EtherFuelItem(new Item.Properties()));
    public static final RegistryObject<Item> ETHER_INGOT = ITEMS.register("ether_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BOTTLED_ETHER = ITEMS.register("bottled_ether", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EXPLOSIVE = ITEMS.register("explosive", () -> new ExplosiveItem(false, new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BLASTING_EXPLOSIVE = ITEMS.register("blasting_explosive", () -> new ExplosiveItem(true, new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> WOODEN_FORGING_HAMMER = ITEMS.register("wooden_forging_hammer", () -> new ForgingHammerItem(Tiers.WOOD,2.0F, -2.8F, new Item.Properties().durability(Tiers.WOOD.getUses())));
    public static final RegistryObject<Item> IRON_FORGING_HAMMER = ITEMS.register("iron_forging_hammer", () -> new ForgingHammerItem(Tiers.IRON,2.0F, -2.8F, new Item.Properties().durability(Tiers.IRON.getUses())));
    public static final RegistryObject<Item> BRONZE_FORGING_HAMMER = ITEMS.register("bronze_forging_hammer", () -> new ForgingHammerItem(ModToolTiers.BRONZE,2.0F, -2.8F, new Item.Properties().durability(ModToolTiers.BRONZE.getUses())));
    public static final RegistryObject<Item> NETHERITE_FORGING_HAMMER = ITEMS.register("netherite_forging_hammer", () -> new ForgingHammerItem(Tiers.NETHERITE,2.0F, -2.8F, new Item.Properties().durability(Tiers.NETHERITE.getUses()).fireResistant()));
    public static final RegistryObject<Item> STEEL_FORGING_HAMMER = ITEMS.register("steel_forging_hammer", () -> new ForgingHammerItem(ModToolTiers.STEEL,2.0F, -2.8F, new Item.Properties().durability(ModToolTiers.STEEL.getUses()).fireResistant()));
    public static final RegistryObject<Item> FLINT_KNIFE = ITEMS.register("flint_knife", () -> new KnifeItem( Tiers.WOOD, new Item.Properties().durability(59)));
    public static final RegistryObject<Item> STONE_KNIFE = ITEMS.register("stone_knife", () -> new KnifeItem(Tiers.STONE, new Item.Properties().durability(131)));
    public static final RegistryObject<Item> IRON_KNIFE = ITEMS.register("iron_knife", () -> new KnifeItem(Tiers.IRON, new Item.Properties().durability(250)));
    public static final RegistryObject<Item> BRONZE_SWORD = ITEMS.register("bronze_sword", () -> new SwordItem(ModToolTiers.BRONZE, 3, -2.4F, new Item.Properties()));
    public static final RegistryObject<Item> BRONZE_PICKAXE = ITEMS.register("bronze_pickaxe", () -> new PickaxeItem(ModToolTiers.BRONZE, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<Item> BRONZE_AXE = ITEMS.register("bronze_axe", () -> new AxeItem(ModToolTiers.BRONZE, 5.0F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> BRONZE_SHOVEL = ITEMS.register("bronze_shovel", () -> new ShovelItem(ModToolTiers.BRONZE, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> BRONZE_HOE = ITEMS.register("bronze_hoe", () -> new HoeItem(ModToolTiers.BRONZE, -3, 0.0F, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_SWORD = ITEMS.register("steel_sword", () -> new SwordItem(ModToolTiers.STEEL, 3, -2.4F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> STEEL_PICKAXE = ITEMS.register("steel_pickaxe", () -> new PickaxeItem(ModToolTiers.STEEL, 1, -2.8F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> STEEL_AXE = ITEMS.register("steel_axe", () -> new AxeItem(ModToolTiers.STEEL, 5.0F, -3.0F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> STEEL_SHOVEL = ITEMS.register("steel_shovel", () -> new ShovelItem(ModToolTiers.STEEL, 1.5F, -3.0F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> STEEL_HOE = ITEMS.register("steel_hoe", () -> new HoeItem(ModToolTiers.STEEL, -4, 0.0F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ETHER_SWORD = ITEMS.register("ether_sword", () -> new SwordItem(ModToolTiers.ETHER_ALLOY, 3, -2.4F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ETHER_PICKAXE = ITEMS.register("ether_pickaxe", () -> new PickaxeItem(ModToolTiers.ETHER_ALLOY, 1, -2.8F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ETHER_AXE = ITEMS.register("ether_axe", () -> new AxeItem(ModToolTiers.ETHER_ALLOY, 5.0F, -3.0F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ETHER_SHOVEL = ITEMS.register("ether_shovel", () -> new ShovelItem(ModToolTiers.ETHER_ALLOY, 1.5F, -3.0F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> ETHER_HOE = ITEMS.register("ether_hoe", () -> new HoeItem(ModToolTiers.ETHER_ALLOY, -5, 0.0F, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> BLAZING_FURNACE = ITEMS.register("blazing_furnace", () -> new BlockItem(ModBlocks.BLAZING_FURNACE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ALLOY_FURNACE = ITEMS.register("alloy_furnace", () -> new BlockItem(ModBlocks.ALLOY_FURNACE.get(), new Item.Properties()));
    public static final RegistryObject<Item> FORGING_TABLE = ITEMS.register("forging_table", () -> new BlockItem(ModBlocks.FORGING_TABLE.get(), new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
