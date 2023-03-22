package com.badkraft.foundations.world.item;

import net.minecraft.world.item.*;


public abstract class ChiselItem extends TieredItem implements Vanishable {
    private static final int atkDmgBase = 1;
    private static final float atkSpdBase = -2.5f;

    protected ChiselItem(Tier tier, Item.Properties itemProperties) {
        super(tier, itemProperties
                .tab(CreativeModeTab.TAB_TOOLS));
    }
}
