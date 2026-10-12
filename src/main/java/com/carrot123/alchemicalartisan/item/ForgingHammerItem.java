
package com.carrot123.alchemicalartisan.item;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;

@SuppressWarnings ({"null", "removal"})
public class ForgingHammerItem extends DiggerItem {

    private static final TagKey<Block> MINEABLE_BLOCKS =
            TagKey.create(
                    Registries.BLOCK,
                    new ResourceLocation(
                            AlchemicalArtisan.MOD_ID,
                            "mineable/forging_hammer"
                    )
            );

    public ForgingHammerItem(
            Tier tier,
            float attackDamage,
            float attackSpeed,
            Properties properties
    ) {
        super(
                attackDamage,
                attackSpeed,
                tier,
                MINEABLE_BLOCKS,
                properties
        );
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack remaining = stack.copy();
        remaining.setCount(1);

        int newDamage = remaining.getDamageValue() + 1;

        if (newDamage >= remaining.getMaxDamage()) {
            return ItemStack.EMPTY;
        }

        remaining.setDamageValue(newDamage);

        return remaining;
    }
}
