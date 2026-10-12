
package com.carrot123.alchemicalartisan.loot;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.registry.ModItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

@SuppressWarnings ({"null", "removal"})
public class AnimalSinewLootModifier extends LootModifier {

    private static final TagKey<EntityType<?>> DROPPERS =
            TagKey.create(
                    Registries.ENTITY_TYPE,
                    new ResourceLocation(
                            AlchemicalArtisan.MOD_ID,
                            "animal_sinew_droppers"
                    )
            );

    public static final Codec<AnimalSinewLootModifier> CODEC =
            RecordCodecBuilder.create(instance ->
                    codecStart(instance).and(instance.group(
                            Codec.FLOAT.fieldOf("base_chance")
                                    .forGetter(modifier -> modifier.baseChance),
                            Codec.FLOAT.fieldOf("looting_bonus")
                                    .forGetter(modifier -> modifier.lootingBonus)
                    )).apply(instance, AnimalSinewLootModifier::new)
            );

    private final float baseChance;
    private final float lootingBonus;

    public AnimalSinewLootModifier(
            LootItemCondition[] conditions,
            float baseChance,
            float lootingBonus
    ) {
        super(conditions);
        this.baseChance = baseChance;
        this.lootingBonus = lootingBonus;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot,
            LootContext context
    ) {
        Entity entity = context.getParamOrNull(
                LootContextParams.THIS_ENTITY
        );

        if (!(entity instanceof LivingEntity)) {
            return generatedLoot;
        }

        if (!entity.getType().is(DROPPERS)) {
            return generatedLoot;
        }

        if (!context.hasParam(LootContextParams.DAMAGE_SOURCE)) {
            return generatedLoot;
        }

        int lootingLevel = Math.max(
                0,
                context.getLootingModifier()
        );

        float chance = Mth.clamp(
                baseChance + lootingBonus * lootingLevel,
                0.0F,
                1.0F
        );

        if (context.getRandom().nextFloat() < chance) {
            generatedLoot.add(
                    new ItemStack(ModItems.ANIMAL_SINEW.get())
            );
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
