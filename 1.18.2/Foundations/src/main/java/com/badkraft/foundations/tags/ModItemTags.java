package com.badkraft.foundations.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;


public class ModItemTags {

    public static final TagKey<Item> FLINT_TOOL_MATERIALS = bind("flint_tool_materials");

    private ModItemTags() {
    }

    private static TagKey<Item> bind(String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(name));
    }
}
