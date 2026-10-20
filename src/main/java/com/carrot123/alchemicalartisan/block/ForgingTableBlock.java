package com.carrot123.alchemicalartisan.block;

import com.carrot123.alchemicalartisan.blockentity.ForgingTableBlockEntity;
import com.carrot123.alchemicalartisan.forging.ForgingToolType;
import com.carrot123.alchemicalartisan.item.ForgingBlueprintItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ForgingTableBlock extends BaseEntityBlock {
    public static final EnumProperty<ForgingToolType> MODE = EnumProperty.create("mode", ForgingToolType.class);

    public ForgingTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
                .setValue(MODE, ForgingToolType.NONE));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
                HorizontalDirectionalBlock.FACING,
                context.getHorizontalDirection().getClockWise());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, MODE);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof ForgingTableBlockEntity table)) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown() && table.hasBlueprint()) {
            if (!level.isClientSide) {
                ItemStack blueprint = table.takeBlueprint();
                if (!player.getInventory().add(blueprint)) {
                    popResource(level, pos, blueprint);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        ItemStack held = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() && !table.hasBlueprint()
                && held.getItem() instanceof ForgingBlueprintItem
                && ForgingBlueprintItem.getToolType(held) != ForgingToolType.NONE) {
            if (!level.isClientSide) {
                ItemStack installed = held.copy();
                installed.setCount(1);
                if (table.installBlueprint(installed)) {
                    held.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
            BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof ForgingTableBlockEntity table) {
                ItemStack blueprint = table.takeBlueprintForDrop();
                if (!blueprint.isEmpty()) {
                    popResource(level, pos, blueprint);
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ForgingTableBlockEntity(pos, state);
    }
}
