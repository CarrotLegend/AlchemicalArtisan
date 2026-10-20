package com.carrot123.alchemicalartisan.registry;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.blockentity.AlloyFurnaceBlockEntity;
import com.carrot123.alchemicalartisan.blockentity.BlazingFurnaceBlockEntity;
import com.carrot123.alchemicalartisan.blockentity.ForgingTableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    ForgeRegistries.BLOCK_ENTITY_TYPES,
                    AlchemicalArtisan.MOD_ID
            );

    public static final RegistryObject<BlockEntityType<BlazingFurnaceBlockEntity>> BLAZING_FURNACE =
            BLOCK_ENTITIES.register("blazing_furnace",
                    () -> BlockEntityType.Builder.of(
                            BlazingFurnaceBlockEntity::new,
                            ModBlocks.BLAZING_FURNACE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<AlloyFurnaceBlockEntity>> ALLOY_FURNACE =
            BLOCK_ENTITIES.register("alloy_furnace",
                    () -> BlockEntityType.Builder.of(
                            AlloyFurnaceBlockEntity::new,
                            ModBlocks.ALLOY_FURNACE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<ForgingTableBlockEntity>> FORGING_TABLE =
            BLOCK_ENTITIES.register("forging_table",
                    () -> BlockEntityType.Builder.of(
                            ForgingTableBlockEntity::new,
                            ModBlocks.FORGING_TABLE.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
