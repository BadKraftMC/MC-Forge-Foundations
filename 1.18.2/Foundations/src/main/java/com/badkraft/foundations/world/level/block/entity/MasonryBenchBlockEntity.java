package com.badkraft.foundations.world.level.block.entity;

import com.badkraft.foundations.world.inventory.MasonryBenchMenu;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class MasonryBenchBlockEntity extends BenchBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    public MasonryBenchBlockEntity(BlockPos blockPos, BlockState blockState) {
        super("Stone Mason's Bench", ModBlockEntities.MASONRY_BENCH_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory, @NotNull Player player) {
        LOGGER.debug("[MASONRY_BENCH_BLOCK_ENTITY] :: server thread ...");
        return new MasonryBenchMenu(containerId, inventory, this);
    }
    @Override
    protected void craftItem() {

    }
    @Override
    protected boolean hasRecipe() {
        return false;
    }
}
