package com.carrot123.alchemicalartisan.recipe;

import com.carrot123.alchemicalartisan.forging.ForgingToolType;
import com.carrot123.alchemicalartisan.item.ForgingBlueprintItem;
import com.carrot123.alchemicalartisan.item.ForgingHammerItem;
import com.carrot123.alchemicalartisan.registry.ModRecipeSerializers;
import com.carrot123.alchemicalartisan.registry.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.TierSortingRegistry;

public class ForgingRecipe implements Recipe<SimpleContainer> {
    public enum BlueprintMaterial {
        PAPER("paper"), PARCHMENT("parchment"), BOTH("both");

        private final String name;

        BlueprintMaterial(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static BlueprintMaterial fromName(String name) {
            for (BlueprintMaterial material : values()) {
                if (material.name.equals(name)) {
                    return material;
                }
            }
            return null;
        }
    }

    private final ResourceLocation id;
    private final ForgingToolType blueprintType;
    private final BlueprintMaterial blueprintMaterial;
    private final Ingredient heatedMetal;
    private final int metalCount;
    private final ResourceLocation minimumHammerTier;
    private final int hitCount;
    private final ItemStack result;

    public ForgingRecipe(ResourceLocation id, ForgingToolType blueprintType,
            BlueprintMaterial blueprintMaterial, Ingredient heatedMetal, int metalCount,
            ResourceLocation minimumHammerTier, int hitCount, ItemStack result) {
        this.id = id;
        this.blueprintType = blueprintType;
        this.blueprintMaterial = blueprintMaterial;
        this.heatedMetal = heatedMetal;
        this.metalCount = metalCount;
        this.minimumHammerTier = minimumHammerTier;
        this.hitCount = hitCount;
        this.result = result.copy();
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (container.getContainerSize() < 3) {
            return false;
        }
        ItemStack blueprint = container.getItem(0);
        ItemStack metal = container.getItem(1);
        ItemStack hammer = container.getItem(2);
        if (!(blueprint.getItem() instanceof ForgingBlueprintItem blueprintItem)
                || ForgingBlueprintItem.getToolType(blueprint) != blueprintType
                || (blueprintMaterial == BlueprintMaterial.PAPER && !blueprintItem.isPaper())
                || (blueprintMaterial == BlueprintMaterial.PARCHMENT && blueprintItem.isPaper())
                || !heatedMetal.test(metal)
                || metal.getCount() < metalCount
                || !(hammer.getItem() instanceof ForgingHammerItem forgingHammer)) {
            return false;
        }
        Tier minimum = TierSortingRegistry.byName(minimumHammerTier);
        Tier actual = forgingHammer.getTier();
        return minimum != null
                && (actual == minimum || TierSortingRegistry.getTiersLowerThan(actual).contains(minimum));
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(heatedMetal);
        return ingredients;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FORGING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.FORGING.get();
    }

    public ForgingToolType getBlueprintType() { return blueprintType; }
    public BlueprintMaterial getBlueprintMaterial() { return blueprintMaterial; }
    public Ingredient getHeatedMetal() { return heatedMetal; }
    public int getMetalCount() { return metalCount; }
    public ResourceLocation getMinimumHammerTier() { return minimumHammerTier; }
    public int getHitCount() { return hitCount; }
}
