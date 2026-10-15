package com.carrot123.alchemicalartisan.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.handler.codec.DecoderException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AlloyRecipeSerializer implements RecipeSerializer<AlloyRecipe> {

    @Override
    public AlloyRecipe fromJson(ResourceLocation id, JsonObject json) {
        JsonArray array = GsonHelper.getAsJsonArray(json, "ingredients");
        if (array.size() < 1 || array.size() > 4) { throw new JsonParseException("Alloy furnace requires 1 to 4 inputs"); }
        List<AlloyRecipe.Input> inputs = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject entry = GsonHelper.convertToJsonObject(array.get(i), "ingredients[" + i + "]");
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(entry, "ingredient"));
            int count = GsonHelper.getAsInt(entry, "count", 1);
            if (count < 1 || count > 64) { throw new JsonParseException("Input count must be 1..64"); }
            CompoundTag nbt = null;
            if (entry.has("nbt")) {
                try { nbt = TagParser.parseTag(GsonHelper.getAsString(entry, "nbt")); }
                catch (CommandSyntaxException exception) { throw new JsonParseException("Invalid input NBT", exception); }
            }
            inputs.add(new AlloyRecipe.Input(ingredient, count, nbt));
        }
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        int fuelLevel = GsonHelper.getAsInt(json, "fuel_level", 1);
        int cookingTime = GsonHelper.getAsInt(json, "cooking_time", 200);
        if (fuelLevel < 1 || fuelLevel > 4) {
            throw new JsonParseException("Alloy fuel level must be 1..4");
        }
        if (cookingTime < 1 || cookingTime > 32767) {
            throw new JsonParseException("Alloy cooking time must be 1..32767 ticks");
        }
        return new AlloyRecipe(id, inputs, result, fuelLevel, cookingTime);
    }

    @Override
    public AlloyRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        if (size < 1 || size > 4) {
            throw new DecoderException("Alloy furnace requires 1 to 4 inputs");
        }
        List<AlloyRecipe.Input> inputs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int count = buffer.readVarInt();
            if (count < 1 || count > 64) {
                throw new DecoderException("Alloy input count must be 1..64");
            }
            boolean hasNbt = buffer.readBoolean();
            CompoundTag nbt = hasNbt ? buffer.readNbt() : null;
            if (hasNbt && nbt == null) {
                throw new DecoderException("Missing alloy input NBT");
            }
            inputs.add(new AlloyRecipe.Input(ingredient, count, nbt));
        }
        ItemStack result = buffer.readItem();
        int fuelLevel = buffer.readVarInt();
        int cookingTime = buffer.readVarInt();
        if (fuelLevel < 1 || fuelLevel > 4) {
            throw new DecoderException("Alloy fuel level must be 1..4");
        }
        if (cookingTime < 1 || cookingTime > 32767) {
            throw new DecoderException("Alloy cooking time must be 1..32767 ticks");
        }
        return new AlloyRecipe(id, inputs, result, fuelLevel, cookingTime);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, AlloyRecipe recipe) {
        buffer.writeVarInt(recipe.getInputs().size());
        for (AlloyRecipe.Input input : recipe.getInputs()) {
            input.ingredient().toNetwork(buffer);
            buffer.writeVarInt(input.count());
            buffer.writeBoolean(input.nbt() != null);
            if (input.nbt() != null) { buffer.writeNbt(input.nbt()); }
        }
        buffer.writeItem(recipe.getResultItem(null));
        buffer.writeVarInt(recipe.getFuelLevel());
        buffer.writeVarInt(recipe.getCookingTime());
    }
}
