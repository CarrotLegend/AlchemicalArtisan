package com.carrot123.alchemicalartisan.item;

import com.carrot123.alchemicalartisan.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class EtherExtractorItem extends Item {

    public EtherExtractorItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.dimension().equals(Level.END)) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.alchemicalartisan.ether_extractor.end_only"),
                        true
                );
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!player.getOffhandItem().is(Items.GLASS_BOTTLE)) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.alchemicalartisan.ether_extractor.need_bottle"),
                        true
                );
            }
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player) || level.isClientSide) {
            return stack;
        }

        if (!level.dimension().equals(Level.END)) {
            return stack;
        }

        if (!player.getMainHandItem().is(this)) {
            return stack;
        }

        ItemStack bottle = player.getOffhandItem();

        if (!bottle.is(Items.GLASS_BOTTLE)) {
            return stack;
        }

        bottle.shrink(1);

        if (bottle.isEmpty()) {
            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        }

        ItemStack result = new ItemStack(ModItems.BOTTLED_ETHER.get());

        if (!player.addItem(result)) {
            player.drop(result, false);
        }

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.BOTTLE_FILL,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );

        return stack;
    }
}
