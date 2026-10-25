package com.carrot123.alchemicalartisan.block;

import com.carrot123.alchemicalartisan.blockentity.BlazingFurnaceBlockEntity;
import com.carrot123.alchemicalartisan.blockentity.GeoBlazingFurnaceBlockEntity;
import com.carrot123.alchemicalartisan.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

@SuppressWarnings ("null")
public class BlazingFurnaceBlock extends BlastFurnaceBlock {

    public BlazingFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(BlockStateProperties.LIT)) {
            return;
        }

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.6D;
        double z = pos.getZ() + 0.5D;
        double offset = 0.52D;
        double sideways = (random.nextDouble() - 0.5D) * 0.6D;
        double particleX = x + facing.getStepX() * offset;
        double particleZ = z + facing.getStepZ() * offset;

        if (facing.getAxis() == Direction.Axis.Z) {
            particleX += sideways;
        } else {
            particleZ += sideways;
        }

        level.addParticle(ParticleTypes.SMOKE, particleX, y, particleZ, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, particleX, y, particleZ, 0.0D, 0.0D, 0.0D);

        if (random.nextDouble() < 0.1D) {
            level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeoBlazingFurnaceBlockEntity(pos, state);
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
