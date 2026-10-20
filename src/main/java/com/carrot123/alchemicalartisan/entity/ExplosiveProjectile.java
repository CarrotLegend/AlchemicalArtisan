package com.carrot123.alchemicalartisan.entity;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.item.ExplosiveItem;
import com.carrot123.alchemicalartisan.registry.ModEntities;
import com.carrot123.alchemicalartisan.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = AlchemicalArtisan.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ExplosiveProjectile extends ThrowableItemProjectile {

    private static final EntityDataAccessor<Boolean> BREAKS_BLOCKS =
            SynchedEntityData.defineId(
                    ExplosiveProjectile.class,
                    EntityDataSerializers.BOOLEAN
            );

    public ExplosiveProjectile(
            EntityType<? extends ExplosiveProjectile> type,
            Level level
    ) {
        super(type, level);
    }

    public ExplosiveProjectile(
            Level level,
            LivingEntity owner,
            boolean breaksBlocks
    ) {
        super(ModEntities.EXPLOSIVE_PROJECTILE.get(), owner, level);

        setBreaksBlocks(breaksBlocks);

        setItem(new ItemStack(
                breaksBlocks
                        ? ModItems.BLASTING_EXPLOSIVE.get()
                        : ModItems.EXPLOSIVE.get()
        ));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BREAKS_BLOCKS, false);
    }

    public boolean breaksBlocks() {
        return entityData.get(BREAKS_BLOCKS);
    }

    public void setBreaksBlocks(boolean value) {
        entityData.set(BREAKS_BLOCKS, value);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.EXPLOSIVE.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (level().isClientSide || isRemoved()) {
            return;
        }

        Level.ExplosionInteraction interaction =
                breaksBlocks()
                        ? Level.ExplosionInteraction.TNT
                        : Level.ExplosionInteraction.NONE;

        level().explode(
                this,
                result.getLocation().x,
                result.getLocation().y,
                result.getLocation().z,
                ExplosiveItem.EXPLOSION_POWER,
                false,
                interaction
        );

        discard();
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && tickCount > 200) {
            discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("BreaksBlocks", breaksBlocks());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setBreaksBlocks(tag.getBoolean("BreaksBlocks"));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (!(event.getSource().getDirectEntity()
                instanceof ExplosiveProjectile projectile)) {
            return;
        }

        event.setAmount(projectile.breaksBlocks() ? 20.0F : 10.0F);
    }
}
