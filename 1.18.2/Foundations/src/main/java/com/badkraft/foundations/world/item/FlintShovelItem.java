package com.badkraft.foundations.world.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;


public class FlintShovelItem extends ShovelItem {
    private static final float atkDmgBase = 0.0f;
    private static final float atkSpdBase = 0.0f;

    public FlintShovelItem() {
        super(ItemTiers.FLINT, atkDmgBase, atkSpdBase, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
    }
}
