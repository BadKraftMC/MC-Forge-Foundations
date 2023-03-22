package com.badkraft.foundations.recipes;

import com.badkraft.foundations.Foundations;
import com.badkraft.foundations.world.level.block.entity.AbstractBenchBlockEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class MasonryBenchRecipe implements Recipe<SimpleContainer> {
    protected static final Logger LOGGER = LogUtils.getLogger();

    private final ResourceLocation container;
    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;

    public MasonryBenchRecipe(ResourceLocation resourceLocation, ItemStack itemResult, NonNullList<Ingredient> recipeItems) {
        LOGGER.debug("[MASONRY_BENCH_RECIPE] :: " + resourceLocation.toString());

        container = resourceLocation;
        output = itemResult;
        //  compulsory items have already been added
        ingredients = recipeItems;
    }

    @Override
    public boolean matches(SimpleContainer itemContainer, Level level) {
        //  is there a better way to do this without creating intermediate list?
        NonNullList<Ingredient> inputItems = NonNullList.withSize(AbstractBenchBlockEntity.MAX_STORAGE_SLOTS, Ingredient.EMPTY);
        int offset = AbstractBenchBlockEntity.INVENTORY_SLOTS_START;
        for (int i = 0; i + offset < AbstractBenchBlockEntity.INVENTORY_SLOTS_END; i++) {
            inputItems.set(i, Ingredient.of(itemContainer.getItem(i + offset)));
        }
        //  add compulsory items to the end
        inputItems.add(inputItems.size(), Ingredient.of(itemContainer.getItem(AbstractBenchBlockEntity.COMPULSORY_SLOT_1)));
        inputItems.add(inputItems.size(), Ingredient.of(itemContainer.getItem(AbstractBenchBlockEntity.COMPULSORY_SLOT_2)));

        return inputItems.containsAll(ingredients);
    }

    @Override
    public ItemStack assemble(SimpleContainer container) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
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
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<MasonryBenchRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "masonry";
    }

    public static class Serializer implements RecipeSerializer<MasonryBenchRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(Foundations.MOD_ID,"masonry");

        @Override
        public MasonryBenchRecipe fromJson(ResourceLocation containerId, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            JsonArray compulsories = GsonHelper.getAsJsonArray(json, "compulsory");

            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size() + compulsories.size(), Ingredient.EMPTY);
            //  add ingredient items
            for (int i = 0; i < ingredients.size(); ++i) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }
            //  add compulsory items
            for (int i = ingredients.size(); i < inputs.size(); ++i) {
                inputs.set(i,Ingredient.fromJson(compulsories.get(i)));
            }

            return new MasonryBenchRecipe(containerId, output, inputs);
        }

        @Override
        public MasonryBenchRecipe fromNetwork(ResourceLocation containerId, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack output = buffer.readItem();
            return new MasonryBenchRecipe(containerId, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MasonryBenchRecipe recipe) {
            buffer.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buffer);
            }

            buffer.writeItemStack(recipe.getResultItem(), false);
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
            return Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
    }
}
