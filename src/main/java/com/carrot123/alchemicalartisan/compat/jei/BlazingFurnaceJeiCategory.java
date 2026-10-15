package com.carrot123.alchemicalartisan.compat.jei;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.blockentity.BlazingFurnaceBlockEntity;
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
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Locale;

public class BlazingFurnaceJeiCategory implements IRecipeCategory<BlastingRecipe> {

    public static final RecipeType<BlastingRecipe> TYPE =
            RecipeType.create(
                    AlchemicalArtisan.MOD_ID,
                    "blazing_furnace",
                    BlastingRecipe.class
            );

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(
                    AlchemicalArtisan.MOD_ID,
                    "textures/jei/blazing_furnace.png"
            );

    private final IDrawable background;
    private final IDrawable image;
    private final IDrawable icon;
    private final List<ItemStack> fuels;

    public BlazingFurnaceJeiCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(176, 112);
        this.image = helper.createDrawable(
                TEXTURE,
                0, 0,
                176, 80
        );
        this.icon = helper.createDrawableItemStack(
                new ItemStack(ModBlocks.BLAZING_FURNACE.get())
        );
        this.fuels = ForgeRegistries.ITEMS.getValues().stream()
                .map(ItemStack::new)
                .filter(stack -> BlazingFurnaceBlockEntity.getFuelDuration(stack) > 0)
                .toList();
    }

    @Override
    public RecipeType<BlastingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
                "block.alchemicalartisan.blazing_furnace"
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
            BlastingRecipe recipe,
            IFocusGroup focuses
    ) {
        builder.addSlot(
                RecipeIngredientRole.INPUT,
                28,
                24
        ).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(
                RecipeIngredientRole.INPUT,
                56,
                58
        ).addItemStacks(fuels);

        builder.addSlot(
                RecipeIngredientRole.OUTPUT,
                124,
                24
        ).addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
    }

    @Override
    public void draw(
            BlastingRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        image.draw(graphics, 0, 0);

        int cookingTicks = Math.max(
                1,
                (recipe.getCookingTime() + 3) / 4
        );

        String seconds = String.format(
                Locale.ROOT,
                "%.2f",
                cookingTicks / 20.0F
        );

        graphics.drawString(
                Minecraft.getInstance().font,
                Component.translatable(
                        "jei.alchemicalartisan.blazing.batch",
                        BlazingFurnaceBlockEntity.getMaxBatch(),
                        seconds
                ),
                7,
                86,
                0x404040,
                false
        );

        graphics.drawString(
                Minecraft.getInstance().font,
                Component.translatable(
                        "jei.alchemicalartisan.blazing.bonus",
                        BlazingFurnaceBlockEntity.getBonusChancePercent()
                ),
                7,
                98,
                0x404040,
                false
        );
    }
}
