package com.badkraft.foundations.world.level.block;

import com.badkraft.foundations.world.level.block.entity.AbstractBenchBlockEntity;
import com.badkraft.foundations.world.level.block.entity.MasonryBenchBlockEntity;
import com.badkraft.foundations.world.level.block.entity.ModBlockEntities;
import com.badkraft.foundations.world.level.block.entity.OvenBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


public class MasonryBenchBlock extends BaseEntityBlock {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public MasonryBenchBlock() {
        super(BlockBehaviour.Properties
                .of(Material.STONE)
                .strength(3.5F)
                .requiresCorrectToolForDrops()
                .noOcclusion());

        registerDefaultState(getStateDefinition().any());
    }

    /* FACING */
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    /* BLOCK ENTITY */
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, BlockState newState,
                         boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof OvenBlockEntity) {
                ((OvenBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(state, level, blockPos, newState, isMoving);
    }
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos blockPos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hitResult) {
        boolean isClientSide = level.isClientSide();

        if(!isClientSide && level.getBlockEntity(blockPos) instanceof MasonryBenchBlockEntity benchBlockEntity) {
            LOGGER.debug("[MASONRY_BENCH_BLOCK] :: rendering thread ...");
            NetworkHooks.openGui(((ServerPlayer)player), benchBlockEntity, blockPos);
        }

        return  InteractionResult.sidedSuccess(isClientSide);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new MasonryBenchBlockEntity(blockPos,blockState);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> entityType) {
        return createTickerHelper(entityType, ModBlockEntities.MASONRY_BENCH_BLOCK_ENTITY.get(),
                AbstractBenchBlockEntity::tick);
    }
}
