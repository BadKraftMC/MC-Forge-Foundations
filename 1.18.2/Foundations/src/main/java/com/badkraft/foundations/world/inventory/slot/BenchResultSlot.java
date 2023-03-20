package com.badkraft.foundations.world.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;


public class BenchResultSlot extends SlotItemHandler {

    public BenchResultSlot(IItemHandler itemHandler, int slotId, int x, int y) {
        super(itemHandler, slotId, x, y);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return  false;
    }
}
