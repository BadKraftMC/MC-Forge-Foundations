package com.badkraft.foundations.core;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ItemReflector {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CATEGORY = "category";
    private static final Class<Item> CLASS = Item.class;

    private final Field category;

    public ItemReflector() {
        LOGGER.debug("Reflecting (" + CLASS + ")");

        Field catField = null;

        try {
            catField = getField(CATEGORY);
        } catch (NoSuchFieldException e) {
            LOGGER.error(e.getMessage(), e);
        }

        category = catField;
    }

    public void setItemCategory(Item item, CreativeModeTab tab) {
        try{
            category.set(item, tab);
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private Field getField(String fieldName) throws NoSuchFieldException {
        List<Field> fields = Arrays.stream(CLASS.getDeclaredFields()).toList();

        fields.forEach((f) -> {
            LOGGER.debug("Item::" + f.getName());
        });

        Field field = fields.get(8); //CLASS.getDeclaredField(fieldName);
        LOGGER.debug("selected field '" + field.getName() + "'");
        field.setAccessible(true);

        return field;
    }
}
