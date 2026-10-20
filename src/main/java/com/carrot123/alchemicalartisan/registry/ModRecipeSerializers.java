package com.carrot123.alchemicalartisan.registry;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.recipe.AlloyRecipe;
import com.carrot123.alchemicalartisan.recipe.AlloyRecipeSerializer;
import com.carrot123.alchemicalartisan.recipe.BlueprintBindingRecipe;
import com.carrot123.alchemicalartisan.recipe.ForgingRecipe;
import com.carrot123.alchemicalartisan.recipe.ForgingRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AlchemicalArtisan.MOD_ID);

    public static final RegistryObject<RecipeSerializer<AlloyRecipe>> ALLOYING =
            SERIALIZERS.register("alloying", AlloyRecipeSerializer::new);

    public static final RegistryObject<RecipeSerializer<BlueprintBindingRecipe>> BLUEPRINT_BINDING =
            SERIALIZERS.register("blueprint_binding", () -> new SimpleCraftingRecipeSerializer<>(BlueprintBindingRecipe::new));

    public static final RegistryObject<RecipeSerializer<ForgingRecipe>> FORGING =
            SERIALIZERS.register("forging", ForgingRecipeSerializer::new);

    public static void register(IEventBus bus) { SERIALIZERS.register(bus); }
}
