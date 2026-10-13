
package com.carrot123.alchemicalartisan.compat.jei;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.client.screen.AlloyFurnaceScreen;
import com.carrot123.alchemicalartisan.client.screen.BlazingFurnaceScreen;
import com.carrot123.alchemicalartisan.registry.ModBlocks;
import com.carrot123.alchemicalartisan.registry.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.List;

@JeiPlugin
public class AlchemicalArtisanJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(
                AlchemicalArtisan.MOD_ID,
                "jei_plugin"
        );
    }

    @Override
    public void registerCategories(
            IRecipeCategoryRegistration registration
    ) {
        var helper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(
                new AlloyFurnaceJeiCategory(helper),
                new BlazingFurnaceJeiCategory(helper)
        );
    }

    @Override
    public void registerRecipes(
            IRecipeRegistration registration
    ) {
        Level level = Minecraft.getInstance().level;

        if (level == null) {
            return;
        }

        RecipeManager manager = level.getRecipeManager();

        registration.addRecipes(
                AlloyFurnaceJeiCategory.TYPE,
                manager.getAllRecipesFor(
                        ModRecipeTypes.ALLOYING.get()
                )
        );

        List<BlastingRecipe> blastingRecipes =
                manager.getAllRecipesFor(
                        net.minecraft.world.item.crafting.RecipeType.BLASTING
                );

        registration.addRecipes(
                BlazingFurnaceJeiCategory.TYPE,
                blastingRecipes
        );
    }

    @Override
    public void registerRecipeCatalysts(
            IRecipeCatalystRegistration registration
    ) {
        registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.ALLOY_FURNACE.get()),
                AlloyFurnaceJeiCategory.TYPE
        );

        registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.BLAZING_FURNACE.get()),
                BlazingFurnaceJeiCategory.TYPE
        );
    }

    @Override
    public void registerGuiHandlers(
            IGuiHandlerRegistration registration
    ) {
        registration.addRecipeClickArea(
                AlloyFurnaceScreen.class,
                95, 30, 29, 20,
                AlloyFurnaceJeiCategory.TYPE
        );

        registration.addRecipeClickArea(
                BlazingFurnaceScreen.class,
                78, 32, 26, 21,
                BlazingFurnaceJeiCategory.TYPE
        );
    }
}
