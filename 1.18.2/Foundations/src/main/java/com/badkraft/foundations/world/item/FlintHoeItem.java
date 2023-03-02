package com.badkraft.foundations.world.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;


public class FlintHoeItem extends HoeItem {
    private static final int atkDmgBase = 0;
    private static final float atkSpdBase = 0.0f;

    public FlintHoeItem() {
        super(ItemTiers.FLINT, atkDmgBase, atkSpdBase, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
    }
}
