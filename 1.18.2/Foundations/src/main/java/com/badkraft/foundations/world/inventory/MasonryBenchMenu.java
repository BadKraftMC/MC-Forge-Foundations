package com.badkraft.foundations.world.inventory;

import com.badkraft.foundations.world.level.block.ModBlocks;
import com.badkraft.foundations.world.level.block.entity.MasonryBenchBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;


public class MasonryBenchMenu extends AbstractBenchMenu {

    public MasonryBenchMenu(int containerId, Inventory inventory, BlockEntity blockEntity) {
        super(ModMenuContainers.MASONRY_CRAFTING_MENU.get(), containerId, inventory, blockEntity);

        getEntity().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(this::setSlotHandlers);
    }

    private MasonryBenchBlockEntity getEntity() {
        return getBenchEntity();
    }

    @Override
    protected Block getBlock() {
        return ModBlocks.MASONRY_BENCH.get();
    }
}
