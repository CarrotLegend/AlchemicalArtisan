package com.carrot123.alchemicalartisan.item;

import com.carrot123.alchemicalartisan.forging.ForgingToolType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ForgingBlueprintItem extends Item {
    public static final String TOOL_TYPE_KEY = "ForgingToolType";

    private final boolean paper;

    public ForgingBlueprintItem(boolean paper, Properties properties) {
        super(properties);
        this.paper = paper;
    }

    public boolean isPaper() {
        return paper;
    }

    public static ForgingToolType getToolType(ItemStack stack) {
        if (!(stack.getItem() instanceof ForgingBlueprintItem)) {
            return ForgingToolType.NONE;
        }
        CompoundTag tag = stack.getTag();
        return tag == null ? ForgingToolType.NONE : ForgingToolType.fromName(tag.getString(TOOL_TYPE_KEY));
    }

    public static void setToolType(ItemStack stack, ForgingToolType type) {
        if (stack.getItem() instanceof ForgingBlueprintItem && type != ForgingToolType.NONE) {
            stack.getOrCreateTag().putString(TOOL_TYPE_KEY, type.getSerializedName());
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        ForgingToolType type = getToolType(stack);
        if (type == ForgingToolType.NONE) {
            return super.getName(stack);
        }
        return Component.translatable(
                "item.alchemicalartisan.forging_blueprint.typed",
                super.getName(stack),
                Component.translatable("forging.alchemicalartisan.type." + type.getSerializedName())
        );
    }
}
