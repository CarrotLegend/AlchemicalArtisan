package com.carrot123.alchemicalartisan.recipe;

import com.carrot123.alchemicalartisan.registry.ModRecipeSerializers;
import com.carrot123.alchemicalartisan.registry.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AlloyRecipe implements Recipe<SimpleContainer> {

    public record Input(Ingredient ingredient, int count, @Nullable CompoundTag nbt) {
        public boolean matches(ItemStack stack) {
            return !stack.isEmpty()
                    && ingredient.test(stack)
                    && stack.getCount() >= count
                    && (nbt == null || Objects.equals(stack.getTag(), nbt));
        }
    }

    private final ResourceLocation id;
    private final List<Input> inputs;
    private final ItemStack result;
    private final int fuelLevel;
    private final int cookingTime;

    public AlloyRecipe(ResourceLocation id, List<Input> inputs, ItemStack result, int fuelLevel, int cookingTime) {
        this.id = id;
        this.inputs = List.copyOf(inputs);
        this.result = result.copy();
        this.fuelLevel = fuelLevel;
        this.cookingTime = cookingTime;
    }

    public List<Input> getInputs() { return inputs; }
    public int getFuelLevel() { return fuelLevel; }
    public int getCookingTime() { return cookingTime; }

    @Nullable
    public int[] findInputSlots(Container container) {
        if (container.getContainerSize() < 4) { return null; }
        int occupied = 0;
        for (int i = 0; i < 4; i++) {
            if (!container.getItem(i).isEmpty()) { occupied++; }
        }
        if (occupied != inputs.size()) { return null; }
        int[] assignment = new int[inputs.size()];
        Arrays.fill(assignment, -1);
        return find(0, container, new boolean[4], assignment) ? assignment : null;
    }

    private boolean find(int inputIndex, Container container, boolean[] used, int[] assignment) {
        if (inputIndex == inputs.size()) { return true; }
        for (int slot = 0; slot < 4; slot++) {
            if (!used[slot] && inputs.get(inputIndex).matches(container.getItem(slot))) {
                used[slot] = true;
                assignment[inputIndex] = slot;
                if (find(inputIndex + 1, container, used, assignment)) { return true; }
                used[slot] = false;
                assignment[inputIndex] = -1;
            }
        }
        return false;
    }

    public void consumeInputs(Container container, int[] matchedSlots) {
        for (int i = 0; i < inputs.size(); i++) {
            container.removeItem(matchedSlots[i], inputs.get(i).count());
        }
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return findInputSlots(container) != null;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= inputs.size();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        inputs.forEach(input -> list.add(input.ingredient()));
        return list;
    }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public RecipeSerializer<?> getSerializer() { return ModRecipeSerializers.ALLOYING.get(); }

    @Override
    public RecipeType<?> getType() { return ModRecipeTypes.ALLOYING.get(); }
}
