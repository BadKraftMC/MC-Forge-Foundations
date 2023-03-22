package com.badkraft.foundations.recipes;

import com.badkraft.foundations.Foundations;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NoOpRecipe  implements Recipe<SimpleContainer> {
    protected static final Logger LOGGER = LogUtils.getLogger();

    private static final String ID = "no-op";

    private final ResourceLocation container;
    private final ItemStack output;
    private final Ingredient input;

    public NoOpRecipe(ResourceLocation resourceLocation, Ingredient recipeItem) {
        LOGGER.debug("[NO_OP_RECIPE] :: " + resourceLocation.toString());

        container = resourceLocation;
        output = recipeItem.getItems() [0];
        input = recipeItem;
    }

    @Override
    public boolean matches(SimpleContainer itemContainer, Level level) {
        return input.test(itemContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer itemContainer) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return container;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return NoOpRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<NoOpRecipe> {
        private Type() { }
        public static final NoOpRecipe.Type INSTANCE = new NoOpRecipe.Type();
        public static final String ID = NoOpRecipe.ID;
    }

    public static class Serializer implements RecipeSerializer<NoOpRecipe> {
        public static final NoOpRecipe.Serializer INSTANCE = new NoOpRecipe.Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(Foundations.MOD_ID,NoOpRecipe.ID);

        @Override
        public NoOpRecipe fromJson(ResourceLocation containerId, JsonObject json) {
            JsonObject item = GsonHelper.getAsJsonObject(json, "item");

            return new NoOpRecipe(containerId, Ingredient.fromJson(item));
        }

        @Nullable
        @Override
        public NoOpRecipe fromNetwork(ResourceLocation containerId, FriendlyByteBuf buffer) {
            Ingredient item = Ingredient.fromNetwork(buffer);

            return new NoOpRecipe(containerId, item);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, NoOpRecipe recipe) {
            recipe.getIngredients().get(0).toNetwork(buffer);
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation containerId) {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return NoOpRecipe.Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
    }
}
