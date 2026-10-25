package com.carrot123.alchemicalartisan.item;

import com.carrot123.alchemicalartisan.entity.ExplosiveProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ExplosiveItem extends Item {

    public static final float THROW_SPEED = 3.0F;
    public static final float EXPLOSION_POWER = 2.0F;
    public static final double EXPLOSION_RADIUS = 4.0D;

    private final boolean breaksBlocks;

    public ExplosiveItem(boolean breaksBlocks, Properties properties) {
        super(properties);
        this.breaksBlocks = breaksBlocks;
    }

    public boolean breaksBlocks() {
        return breaksBlocks;
    }

    public static float getChargePower(int useTicks) {
        return BowItem.getPowerForTime(Math.max(0, useTicks));
    }

    public static float getThrowSpeed(int useTicks) {
        return THROW_SPEED * getChargePower(useTicks);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(
            ItemStack stack,
            Level level,
            LivingEntity entity,
            int timeLeft
    ) {
        if (!(entity instanceof Player player) || level.isClientSide) {
            return;
        }

        int useTicks = getUseDuration(stack) - timeLeft;
        float chargePower = getChargePower(useTicks);

        if (chargePower < 0.1F) {
            return;
        }

        ExplosiveProjectile projectile =
                new ExplosiveProjectile(level, player, breaksBlocks);

        projectile.shootFromRotation(
                player,
                player.getXRot(),
                player.getYRot(),
                0.0F,
                getThrowSpeed(useTicks),
                0.0F
        );

        if (!level.addFreshEntity(projectile)) {
            return;
        }

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.SNOWBALL_THROW,
                SoundSource.PLAYERS,
                0.5F,
                0.6F
        );

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
    }
}
