
package com.carrot123.alchemicalartisan.compat.jei;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.recipe.AlloyRecipe;
import com.carrot123.alchemicalartisan.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlloyFurnaceJeiCategory implements IRecipeCategory<AlloyRecipe> {

    public static final RecipeType<AlloyRecipe> TYPE =
            RecipeType.create(
                    AlchemicalArtisan.MOD_ID,
                    "alloy_furnace",
                    AlloyRecipe.class
            );

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(
                    AlchemicalArtisan.MOD_ID,
                    "textures/jei/alloy_furnace.png"
            );

    private final IDrawable background;
    private final IDrawable image;
    private final IDrawable icon;

    public AlloyFurnaceJeiCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(176, 88);
        this.image = helper.createDrawable(
                TEXTURE, 0, 0, 176, 62
        );
        this.icon = helper.createDrawableItemStack(
                new ItemStack(ModBlocks.ALLOY_FURNACE.get())
        );
    }

    @Override
    public RecipeType<AlloyRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
                "block.alchemicalartisan.alloy_furnace"
        );
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
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            AlloyRecipe recipe,
            IFocusGroup focuses
    ) {
        List<AlloyRecipe.Input> inputs = recipe.getInputs();

        for (int i = 0; i < inputs.size() && i < 4; i++) {
            AlloyRecipe.Input input = inputs.get(i);
            List<ItemStack> stacks = new ArrayList<>();

            for (ItemStack candidate : input.ingredient().getItems()) {
                ItemStack stack = candidate.copy();
                stack.setCount(input.count());

                if (input.nbt() != null) {
                    stack.setTag(input.nbt().copy());
                } else {
                    stack.setTag(null);
                }

                stacks.add(stack);
            }

            builder.addSlot(
                    RecipeIngredientRole.INPUT,
                    21 + i * 18,
                    14
            ).addItemStacks(stacks);
        }

        builder.addSlot(
                RecipeIngredientRole.OUTPUT,
                135,
                31
        ).addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
    }

    @Override
    public void draw(
            AlloyRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        image.draw(graphics, 0, 0);

        String level = switch (recipe.getFuelLevel()) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            default -> "?";
        };

        String seconds = String.format(
                Locale.ROOT,
                "%.1f",
                recipe.getCookingTime() / 20.0F
        );

        graphics.drawString(
                Minecraft.getInstance().font,
                Component.translatable(
                        "jei.alchemicalartisan.alloy.fuel_level",
                        level
                ),
                7,
                65,
                0x6B4710,
                false
        );

        graphics.drawString(
                Minecraft.getInstance().font,
                Component.translatable(
                        "jei.alchemicalartisan.alloy.time",
                        seconds
                ),
                7,
                76,
                0x6B4710,
                false
        );
    }
}
