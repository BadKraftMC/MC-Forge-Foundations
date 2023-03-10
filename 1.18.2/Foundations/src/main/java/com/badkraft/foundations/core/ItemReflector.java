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
    private static final int CATEGORY = 8;  // "category";
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

    @SuppressWarnings("SameParameterValue")
    private Field getField(int fieldIndex) throws NoSuchFieldException {
        List<Field> fields = Arrays.stream(CLASS.getDeclaredFields()).toList();

        //  just so we know what the fields are - cross-index from running inside IDE
        fields.forEach((f) -> {
            LOGGER.debug("Item::" + f.getName());
        });

        Field field = fields.get(fieldIndex); //CLASS.getDeclaredField(fieldName);
        LOGGER.debug("selected field '" + field.getName() + "'");
        field.setAccessible(true);

        return field;
    }
}
