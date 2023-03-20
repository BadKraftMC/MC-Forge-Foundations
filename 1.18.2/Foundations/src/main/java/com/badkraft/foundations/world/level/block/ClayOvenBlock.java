package com.badkraft.foundations.world.level.block;


import com.badkraft.foundations.world.level.block.entity.ModBlockEntities;
import com.badkraft.foundations.world.level.block.entity.OvenBlockEntity;
import com.badkraft.foundations.world.level.block.state.properties.ModBlockStateProperties;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.Random;
import java.util.function.ToIntFunction;


public class ClayOvenBlock  extends BaseEntityBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int GLOW_VALUE = 8;

    private final boolean spawnParticles;

    private Direction blockFront;

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty SMELTING = BlockStateProperties.LIT;
    public static final BooleanProperty COOKING = ModBlockStateProperties.SMOLDER;
    public static final BooleanProperty SIGNAL_FIRE = BlockStateProperties.SIGNAL_FIRE;

    /* CONSTRUCTOR */
    public ClayOvenBlock() {
        super(BlockBehaviour.Properties
                .of(Material.STONE)
                .strength(3.5F)
                .requiresCorrectToolForDrops()
                .lightLevel(litOvenEmission(15))
                .noOcclusion());

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SMELTING, Boolean.FALSE)
                .setValue(COOKING, Boolean.FALSE));

        spawnParticles = true;
    }

    /* BLOCK STATE */
    @Override
    public void animateTick(BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Random random) {
        double oX = (double)blockPos.getX() + 0.5D;
        double oY = blockPos.getY();
        double oZ = (double)blockPos.getZ() + 0.5D;

        if (state.getValue(COOKING)) {
            LOGGER.debug("[animateTick] :: Cooking");
            if (random.nextInt(10) == 0) {
                level.playLocalSound(oX, oY + 0.5D, oZ, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
            }

            if (spawnParticles && random.nextInt(5) == 0) {
                for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                    level.addParticle(ParticleTypes.SMOKE, oX, oY + 1.1D, oZ, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        if (state.getValue(SMELTING)) {
            double offset;

            LOGGER.debug("[animateTick] :: Smelting");
            if (random.nextDouble() < 0.1D) {
                level.playLocalSound(oX, oY, oZ, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }
            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();

            offset = random.nextDouble() * 0.6D - 0.3D;
            double xOffset = axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52D : offset;
            offset = random.nextDouble() * 6.0D / 16.0D;
            double zOffset = axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52D : offset;
            level.addParticle(ParticleTypes.FLAME, oX + xOffset, oY + offset, oZ + zOffset, 0.0D, 0.0D, 0.0D);
        }
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SIGNAL_FIRE, COOKING, SMELTING);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(SIGNAL_FIRE, isSmokeSource(levelaccessor.getBlockState(blockpos.below())));
    }
    //	Gets a light emission level for oven block state. If no added fuel, light emits from embers glow.
    private static ToIntFunction<BlockState> litOvenEmission(int value) {
        return (blockState) -> blockState.getValue(SMELTING) ? value : GLOW_VALUE;
    }

    /* BEHAVIORS */
    private boolean isSmokeSource(BlockState blockState) {
        return blockState.is(Blocks.HAY_BLOCK);
    }

    /* FACING */
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return setBlockFacing(state.setValue(FACING, rotation.rotate(state.getValue(FACING))));
    }
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos,
                                        @NotNull CollisionContext context) {
        return SHAPE;
    }

    private BlockState setBlockFacing(BlockState state) {
        blockFront = state.getValue(FACING);

        return  state;
    }
    private boolean hitTopFace(BlockHitResult hitResult) {
        return hitResult.getDirection() == Direction.UP;
    }
    private boolean hitFrontFace(BlockHitResult hitResult) {
        return hitResult.getDirection() == blockFront;
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

        if (!isClientSide && level.getBlockEntity(blockPos) instanceof OvenBlockEntity ovenEntity) {
            String playerMessage = null;

            if(hitTopFace(hitResult)) {
                //  cooking
                //  get the item in player's interacting hand
                ItemStack cookItemStack = player.getItemInHand(hand);
                Optional< CampfireCookingRecipe> recipe = ovenEntity.getCookableRecipe(cookItemStack);

                if(recipe.isPresent()) {
                    chatDebug(player, "Cook state (" + state.getValue(COOKING) + ")");
                    //  cook
                    if(ovenEntity.placeFood(player.getAbilities().instabuild ? cookItemStack.copy() : cookItemStack, recipe.get().getCookingTime())) {
                        // TODO: award custom stats (player.awardStat(ModStats.INTERACT_WITH_CLAY_OVEN);)
                        if(!state.getValue(COOKING)) {
                            state.setValue(COOKING, true);
                            chatDebug(player, "Cook state (" + state.getValue(COOKING) + ")");
                        }

                        return InteractionResult.SUCCESS;
                    }

                    return InteractionResult.CONSUME;
                }
                else {
                    if(!ovenEntity.hasCookingSlots()) {
                        playerMessage = "Oven top is full";
                    }
                    else {
                        playerMessage = cookItemStack.getDisplayName().getString() + " is not a cookable item";
                    }
                }
            }
            else {
                //  attempt smelting
                NetworkHooks.openGui(((ServerPlayer)player), ovenEntity, blockPos);
            }
            chatDebug(player, playerMessage);
        }

        return InteractionResult.sidedSuccess(isClientSide);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState state) {
        return new OvenBlockEntity(blockPos, state);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> entityType) {
        if(level.isClientSide) {
            return createTickerHelper(entityType, ModBlockEntities.OVEN_BLOCK_ENTITY.get(), OvenBlockEntity::particleTick);
        } else {
            return createTickerHelper(entityType, ModBlockEntities.OVEN_BLOCK_ENTITY.get(),
                    (l, pos, s, entity) -> {
                        OvenBlockEntity.cookTick(l, pos, s, entity);
                        OvenBlockEntity.smeltTick(l, pos, s, entity);
                    });
        }
    }

    private void chatDebug(Player player, String message) {
        if(message == null) {
            return;
        }

        TextComponent text = new TextComponent("[debug]: " + message);
        player.sendMessage(text, player.getUUID());
    }
}
