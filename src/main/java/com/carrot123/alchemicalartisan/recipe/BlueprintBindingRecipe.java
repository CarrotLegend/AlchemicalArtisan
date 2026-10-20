package com.carrot123.alchemicalartisan.recipe;

import com.carrot123.alchemicalartisan.forging.ForgingToolType;
import com.carrot123.alchemicalartisan.item.ForgingBlueprintItem;
import com.carrot123.alchemicalartisan.registry.ModRecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BlueprintBindingRecipe extends CustomRecipe {
    public BlueprintBindingRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return findParts(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        Parts parts = findParts(container);
        if (parts == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = container.getItem(parts.blueprintSlot()).copy();
        result.setCount(1);
        ForgingBlueprintItem.setToolType(result, parts.type());
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        Parts parts = findParts(container);
        if (parts != null) {
            remaining.set(parts.toolSlot(), container.getItem(parts.toolSlot()).copy());
        }
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BLUEPRINT_BINDING.get();
    }

    private Parts findParts(CraftingContainer container) {
        int blueprintSlot = -1;
        int toolSlot = -1;
        ForgingToolType type = ForgingToolType.NONE;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof ForgingBlueprintItem) {
                if (blueprintSlot >= 0) {
                    return null;
                }
                blueprintSlot = slot;
                continue;
            }
            ForgingToolType candidate = ForgingToolType.fromTool(stack.getItem());
            if (candidate == ForgingToolType.NONE || toolSlot >= 0 || stack.getCount() != 1) {
                return null;
            }
            toolSlot = slot;
            type = candidate;
        }
        return blueprintSlot >= 0 && toolSlot >= 0 ? new Parts(blueprintSlot, toolSlot, type) : null;
    }

    private record Parts(int blueprintSlot, int toolSlot, ForgingToolType type) {
    }
}
