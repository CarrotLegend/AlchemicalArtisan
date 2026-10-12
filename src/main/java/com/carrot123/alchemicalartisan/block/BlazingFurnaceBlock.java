
package com.carrot123.alchemicalartisan.block;

import com.carrot123.alchemicalartisan.blockentity.BlazingFurnaceBlockEntity;
import com.carrot123.alchemicalartisan.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

@SuppressWarnings ("null")
public class BlazingFurnaceBlock extends BlastFurnaceBlock {

    public BlazingFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlazingFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide
                ? null
                : createTickerHelper(
                        type,
                        ModBlockEntities.BLAZING_FURNACE.get(),
                        BlazingFurnaceBlockEntity::serverTick
                );
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof BlazingFurnaceBlockEntity) {
                player.openMenu((BlazingFurnaceBlockEntity) blockEntity);
                player.awardStat(Stats.INTERACT_WITH_BLAST_FURNACE);
            }
        }
    }
}
