package com.badkraft.foundations.world.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;


public class MedallionItem  extends DiggerItem {
    private static final float atkDmgBase = 0.0F;
    private static final float atkSpdBase = 0.0f;

    public MedallionItem() {
        super(atkDmgBase, atkSpdBase, ItemTiers.PERMANENT, BlockTags.FIRE, new Item.Properties());
    }
}
