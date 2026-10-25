package com.carrot123.alchemicalartisan.registry;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.block.AlloyFurnaceBlock;
import com.carrot123.alchemicalartisan.block.BlazingFurnaceBlock;
import com.carrot123.alchemicalartisan.block.ForgingTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, AlchemicalArtisan.MOD_ID);

    public static final RegistryObject<Block> BLAZING_FURNACE = BLOCKS.register("blazing_furnace", () -> new BlazingFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.BLAST_FURNACE)));
    public static final RegistryObject<Block> ALLOY_FURNACE = BLOCKS.register("alloy_furnace", () -> new AlloyFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.BLAST_FURNACE)));
    public static final RegistryObject<Block> FORGING_TABLE = BLOCKS.register("forging_table", () -> new ForgingTableBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).noOcclusion()));
    public static final RegistryObject<Block> BRONZE_BLOCK = BLOCKS.register("bronze_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> STEEL_BLOCK = BLOCKS.register("steel_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> ETHER_BLOCK = BLOCKS.register("ether_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK)));
    public static final RegistryObject<Block> TIN_BLOCK = BLOCKS.register("tin_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistryObject<Block> TIN_ORE = BLOCKS.register("tin_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> DEEPSLATE_TIN_ORE = BLOCKS.register("deepslate_tin_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
