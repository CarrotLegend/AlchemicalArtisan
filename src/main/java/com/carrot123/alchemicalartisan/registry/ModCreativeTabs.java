package com.carrot123.alchemicalartisan.registry;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings ({"null", "unused"})
public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            AlchemicalArtisan.MOD_ID
        );

    public static final RegistryObject<CreativeModeTab> EQUIPMENT =
        CREATIVE_TABS.register("equipment",
            () -> CreativeModeTab.builder()
                .title(Component.translatable(
                    "creativetab.alchemicalartisan.equipment"
                ))
                .icon(() -> new ItemStack(
                    ModItems.IRON_FORGING_HAMMER.get()
                ))
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.WOODEN_FORGING_HAMMER.get());
                    output.accept(ModItems.IRON_FORGING_HAMMER.get());
                    output.accept(ModItems.BRONZE_FORGING_HAMMER.get());
                    output.accept(ModItems.NETHERITE_FORGING_HAMMER.get());
                    output.accept(ModItems.STEEL_FORGING_HAMMER.get());
                })
                .build()
        );

    public static final RegistryObject<CreativeModeTab> MATERIALS =
        CREATIVE_TABS.register("materials",
            () -> CreativeModeTab.builder()
                .title(Component.translatable(
                    "creativetab.alchemicalartisan.materials"
                ))
                .icon(() -> new ItemStack(
                    ModItems.ANIMAL_SINEW.get()
                ))
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.PAPER_FORGING_BLUEPRINT.get());
                    output.accept(ModItems.PARCHMENT_FORGING_BLUEPRINT.get());
                    output.accept(ModItems.HERBAL_FIBER.get());
                    output.accept(ModItems.ANIMAL_SINEW.get());
                    output.accept(ModItems.PROCESSED_SINEW.get());
                    output.accept(ModItems.CRUDE_ADHESIVE.get());
                    output.accept(ModItems.ADHESIVE.get());
                    output.accept(ModItems.UNIVERSAL_REPAIR_MATERIAL.get());
                    output.accept(ModItems.STEEL_INGOT.get());
                    output.accept(ModItems.ETHER_EXTRACTOR.get());
                    output.accept(ModItems.ETHER_FUEL.get());
                    output.accept(ModItems.ETHER_INGOT.get());
                    output.accept(ModItems.BOTTLED_ETHER.get());
                })
                .build()
        );

    public static final RegistryObject<CreativeModeTab> BLOCKS =
        CREATIVE_TABS.register("blocks",
            () -> CreativeModeTab.builder()
                .title(Component.translatable(
                    "creativetab.alchemicalartisan.blocks"
                ))
                .icon(() -> new ItemStack(
                    ModItems.BLAZING_FURNACE.get()
                ))
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.BLAZING_FURNACE.get());
                    output.accept(ModItems.ALLOY_FURNACE.get());
                })
                .build()
        );

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
