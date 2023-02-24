package com.badkraft.foundations.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.ToIntFunction;


public class ClayOvenBlock  extends Block {
    private static final int GLOW_VALUE = 8;

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public ClayOvenBlock() {
        super(BlockBehaviour.Properties
                .of(Material.STONE)
                .strength(3.5F)
                .requiresCorrectToolForDrops()
                .lightLevel(litOvenEmission(15))
                .noOcclusion());

        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.FALSE));
    }

    /* BLOCKSTATE */
    //	Gets a light emission level for oven block state. If no added fuel, light emits from embers glow.
    private static ToIntFunction<BlockState> litOvenEmission(int value) {
        return (blockState) -> {
            return blockState.getValue(BlockStateProperties.LIT) ? value : GLOW_VALUE;
        };
    }

    /* FACING */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    @Override
    public  BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }
    @Override
    public  BlockState mirror(BlockState pState, Mirror pMirror) {
        //noinspection deprecation
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT);
    }
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
