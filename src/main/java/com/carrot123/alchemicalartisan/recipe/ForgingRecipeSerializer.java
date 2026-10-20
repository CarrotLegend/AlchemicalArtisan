package com.carrot123.alchemicalartisan.recipe;

import com.carrot123.alchemicalartisan.forging.ForgingToolType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.TierSortingRegistry;

public class ForgingRecipeSerializer implements RecipeSerializer<ForgingRecipe> {
    @Override
    public ForgingRecipe fromJson(ResourceLocation id, JsonObject json) {
        ForgingToolType type = ForgingToolType.fromName(GsonHelper.getAsString(json, "blueprint_type"));
        ForgingRecipe.BlueprintMaterial material = ForgingRecipe.BlueprintMaterial.fromName(
                GsonHelper.getAsString(json, "blueprint_material"));
        Ingredient metal = Ingredient.fromJson(json.get("heated_metal"));
        int metalCount = GsonHelper.getAsInt(json, "metal_count");
        ResourceLocation minimumTier = ResourceLocation.tryParse(GsonHelper.getAsString(json, "minimum_hammer_tier"));
        int hitCount = GsonHelper.getAsInt(json, "hit_count");
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        if (!isValid(type, material, metal, metalCount, minimumTier, hitCount, result)) {
            throw new JsonParseException("Invalid forging recipe fields: " + id);
        }
        return new ForgingRecipe(id, type, material, metal, metalCount, minimumTier, hitCount, result);
    }

    @Override
    public ForgingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        ForgingToolType type = ForgingToolType.fromName(buffer.readUtf(16));
        ForgingRecipe.BlueprintMaterial material = ForgingRecipe.BlueprintMaterial.fromName(buffer.readUtf(16));
        Ingredient metal = Ingredient.fromNetwork(buffer);
        int metalCount = buffer.readVarInt();
        ResourceLocation minimumTier = buffer.readResourceLocation();
        int hitCount = buffer.readVarInt();
        ItemStack result = buffer.readItem();
        if (!isValid(type, material, metal, metalCount, minimumTier, hitCount, result)) {
            throw new DecoderException("Invalid forging recipe fields: " + id);
        }
        return new ForgingRecipe(id, type, material, metal, metalCount, minimumTier, hitCount, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, ForgingRecipe recipe) {
        buffer.writeUtf(recipe.getBlueprintType().getSerializedName());
        buffer.writeUtf(recipe.getBlueprintMaterial().getName());
        recipe.getHeatedMetal().toNetwork(buffer);
        buffer.writeVarInt(recipe.getMetalCount());
        buffer.writeResourceLocation(recipe.getMinimumHammerTier());
        buffer.writeVarInt(recipe.getHitCount());
        buffer.writeItem(recipe.getResultItem(null));
    }

    private static boolean isValid(ForgingToolType type, ForgingRecipe.BlueprintMaterial material,
            Ingredient metal, int metalCount, ResourceLocation minimumTier,
            int hitCount, ItemStack result) {
        return type != ForgingToolType.NONE
                && material != null
                && !metal.isEmpty()
                && metalCount >= 1 && metalCount <= 64
                && minimumTier != null && TierSortingRegistry.byName(minimumTier) != null
                && hitCount >= 1 && hitCount <= 32767
                && !result.isEmpty() && result.getCount() <= result.getMaxStackSize();
    }
}
