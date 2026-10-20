package com.carrot123.alchemicalartisan.forging;

import com.carrot123.alchemicalartisan.item.KnifeItem;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;

public enum ForgingToolType implements StringRepresentable {
    NONE("none"),
    SWORD("sword"),
    PICKAXE("pickaxe"),
    AXE("axe"),
    HOE("hoe"),
    SHOVEL("shovel");

    private final String name;

    ForgingToolType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static ForgingToolType fromName(String name) {
        for (ForgingToolType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return NONE;
    }

    public static ForgingToolType fromTool(Item item) {
        if (item instanceof KnifeItem) {
            return NONE;
        }
        if (item instanceof SwordItem) {
            return SWORD;
        }
        if (item instanceof PickaxeItem) {
            return PICKAXE;
        }
        if (item instanceof AxeItem) {
            return AXE;
        }
        if (item instanceof HoeItem) {
            return HOE;
        }
        if (item instanceof ShovelItem) {
            return SHOVEL;
        }
        return NONE;
    }
}
