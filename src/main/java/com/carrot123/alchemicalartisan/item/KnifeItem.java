
package com.carrot123.alchemicalartisan.item;

import com.carrot123.alchemicalartisan.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class KnifeItem extends SwordItem {

    public KnifeItem(Tier tier, Properties properties) {
        super(tier, 1, -2.2F, properties);
    }

    @Override
    public boolean mineBlock(
            ItemStack stack,
            Level level,
            BlockState state,
            BlockPos pos,
            LivingEntity miner
    ) {
        if (state.is(Blocks.GRASS) || state.is(Blocks.TALL_GRASS)) {
            if (!level.isClientSide) {
                Block.popResource(
                        level,
                        pos,
                        new ItemStack(ModItems.HERBAL_FIBER.get())
                );

                stack.hurtAndBreak(
                        1,
                        miner,
                        entity -> entity.broadcastBreakEvent(
                                EquipmentSlot.MAINHAND
                        )
                );
            }

            return true;
        }

        return super.mineBlock(stack, level, state, pos, miner);
    }
}
