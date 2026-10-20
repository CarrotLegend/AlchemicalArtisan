package com.carrot123.alchemicalartisan.registry;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.entity.ExplosiveProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(
                    ForgeRegistries.ENTITY_TYPES,
                    AlchemicalArtisan.MOD_ID
            );

    public static final RegistryObject<EntityType<ExplosiveProjectile>>
            EXPLOSIVE_PROJECTILE = ENTITY_TYPES.register(
                    "explosive_projectile",
                    () -> EntityType.Builder
                            .<ExplosiveProjectile>of(
                                    ExplosiveProjectile::new,
                                    MobCategory.MISC
                            )
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(8)
                            .updateInterval(10)
                            .build("alchemicalartisan:explosive_projectile")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
