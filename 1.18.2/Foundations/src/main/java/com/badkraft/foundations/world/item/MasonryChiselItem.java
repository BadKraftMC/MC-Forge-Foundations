package com.badkraft.foundations.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;


public class MasonryChiselItem extends ChiselItem {

    protected MasonryChiselItem(Tier tier, int durability) {
        super(tier, new Item.Properties().durability(durability));
    }
}
