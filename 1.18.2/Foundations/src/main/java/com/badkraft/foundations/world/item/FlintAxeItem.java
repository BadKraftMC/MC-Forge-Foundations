package com.badkraft.foundations.world.item;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;


public class FlintAxeItem extends AxeItem {
    private static final int atkDmgBase = 1;
    private static final float atkSpdBase = -2.5f;

    public FlintAxeItem() {
        super(ItemTiers.FLINT, atkDmgBase, atkSpdBase, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
    }
}
