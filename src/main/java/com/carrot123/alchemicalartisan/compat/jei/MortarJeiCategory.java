package com.carrot123.alchemicalartisan.compat.jei;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.recipe.GrindingRecipe;
import com.carrot123.alchemicalartisan.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MortarJeiCategory implements IRecipeCategory<GrindingRecipe> {
    public static final RecipeType<GrindingRecipe> TYPE =
            RecipeType.create(AlchemicalArtisan.MOD_ID, "grinding", GrindingRecipe.class);

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AlchemicalArtisan.MOD_ID, "textures/jei/mortar.png");

    private final IDrawable background;
    private final IDrawable image;
    private final IDrawable icon;

    public MortarJeiCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(112, 64);
        this.image = helper.createDrawable(TEXTURE, 0, 0, 112, 64);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModBlocks.MORTAR.get()));
    }

    @Override
    public RecipeType<GrindingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.alchemicalartisan.mortar");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GrindingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 26, 24)
                .addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 70, 24)
                .addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
    }

    @Override
    public void draw(GrindingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        image.draw(graphics, 0, 0);
    }
}