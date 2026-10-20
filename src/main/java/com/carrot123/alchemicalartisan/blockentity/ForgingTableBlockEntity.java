package com.carrot123.alchemicalartisan.blockentity;

import com.carrot123.alchemicalartisan.block.ForgingTableBlock;
import com.carrot123.alchemicalartisan.forging.ForgingToolType;
import com.carrot123.alchemicalartisan.item.ForgingBlueprintItem;
import com.carrot123.alchemicalartisan.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ForgingTableBlockEntity extends BlockEntity {
    private static final String BLUEPRINT_KEY = "Blueprint";

    private ItemStack blueprint = ItemStack.EMPTY;

    public ForgingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FORGING_TABLE.get(), pos, state);
    }

    public boolean hasBlueprint() {
        return !blueprint.isEmpty();
    }

    public boolean installBlueprint(ItemStack stack) {
        if (hasBlueprint() || !(stack.getItem() instanceof ForgingBlueprintItem)
                || ForgingBlueprintItem.getToolType(stack) == ForgingToolType.NONE) {
            return false;
        }
        blueprint = stack.copy();
        blueprint.setCount(1);
        syncState();
        return true;
    }

    public ItemStack takeBlueprint() {
        ItemStack result = blueprint;
        blueprint = ItemStack.EMPTY;
        syncState();
        return result;
    }

    public ItemStack takeBlueprintForDrop() {
        ItemStack result = blueprint;
        blueprint = ItemStack.EMPTY;
        setChanged();
        return result;
    }

    public ForgingToolType getMode() {
        return ForgingBlueprintItem.getToolType(blueprint);
    }

    // 仅供后续真实锻造成功结算调用，本次没有调用路径。
    public boolean consumeBlueprintUseAfterSuccessfulForge() {
        if (!(blueprint.getItem() instanceof ForgingBlueprintItem item)) {
            return false;
        }
        if (item.isPaper()) {
            int damage = blueprint.getDamageValue() + 1;
            if (damage >= blueprint.getMaxDamage()) {
                blueprint = ItemStack.EMPTY;
            } else {
                blueprint.setDamageValue(damage);
            }
        }
        syncState();
        return true;
    }

    private void syncState() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            ForgingToolType mode = getMode();
            if (state.hasProperty(ForgingTableBlock.MODE) && state.getValue(ForgingTableBlock.MODE) != mode) {
                level.setBlock(worldPosition, state.setValue(ForgingTableBlock.MODE, mode), 3);
            }
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            syncState();
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        blueprint = tag.contains(BLUEPRINT_KEY, 10)
                ? ItemStack.of(tag.getCompound(BLUEPRINT_KEY))
                : ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!blueprint.isEmpty()) {
            tag.put(BLUEPRINT_KEY, blueprint.save(new CompoundTag()));
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            load(tag);
        }
    }
}
