
package com.carrot123.alchemicalartisan.registry;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.recipe.AlloyRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(
                    ForgeRegistries.RECIPE_TYPES,
                    AlchemicalArtisan.MOD_ID
            );

    public static final RegistryObject<RecipeType<AlloyRecipe>> ALLOYING =
            RECIPE_TYPES.register("alloying",
                    () -> new RecipeType<AlloyRecipe>() {
                        @Override
                        public String toString() {
                            return AlchemicalArtisan.MOD_ID + ":alloying";
                        }
                    }
            );

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}
