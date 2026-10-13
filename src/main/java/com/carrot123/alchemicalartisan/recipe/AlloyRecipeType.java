package com.carrot123.alchemicalartisan.recipe;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class AlloyRecipeType implements RecipeType<AlloyRecipe> {

    public static final AlloyRecipeType INSTANCE = new AlloyRecipeType();
    public static final String ID = "alloying";

    @Override
    public String toString() {
        return new ResourceLocation(AlchemicalArtisan.MOD_ID, ID).toString();
    }
}