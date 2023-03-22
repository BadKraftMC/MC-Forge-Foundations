package com.badkraft.foundations.recipes;

import com.badkraft.foundations.Foundations;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Foundations.MOD_ID);

    public static final RegistryObject<RecipeSerializer<NoOpRecipe>> NO_OP_SERIALIZER =
            SERIALIZERS.register("no-op", () -> NoOpRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<MasonryBenchRecipe>> MASONRY_SERIALIZER =
            SERIALIZERS.register("masonry", () -> MasonryBenchRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
