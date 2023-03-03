package com.badkraft.foundations.world.level.block.entity;

import com.badkraft.foundations.world.level.block.ClayOvenBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;


public class OvenBlockEntity extends AbstractFurnaceBlockEntity implements MenuProvider {
    private static final int MAX_COOKING_SLOTS = 4;

    private final int[] cookingProgress = new int[MAX_COOKING_SLOTS];
    private final int[] cookingTime = new int[MAX_COOKING_SLOTS];
    private int cookingCount = 0;

    private final NonNullList<ItemStack> cookItems = NonNullList.withSize(MAX_COOKING_SLOTS, ItemStack.EMPTY);
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot){
            setChanged();
        }
    };

    public OvenBlockEntity(BlockPos blockPos, BlockState state) {
        super(ModBlockEntities.OVEN_BLOCK_ENTITY.get(), blockPos, state, RecipeType.SMELTING);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent("Clay Oven");
    }
    @Override
    protected @NotNull Component getDefaultName() {
        return getDisplayName();
    }
    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new FurnaceMenu(containerId, inventory, this, dataAccess);
    }
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    public Optional<CampfireCookingRecipe> getCookableRecipe(ItemStack itemStack) {
        if (cookItems.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        } else {
            assert level != null;
            return level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SimpleContainer(itemStack), level);
        }
    }
    public boolean placeFood(ItemStack cookItemStack, int cookTime) {
        for(int i = 0; i < cookItems.size(); ++i) {
            ItemStack itemStack = cookItems.get(i);
            if(itemStack.isEmpty()) {
                cookingTime[i] = cookTime;
                cookingProgress[i] = 0;
                cookItems.set(i, cookItemStack.split(1));
                //  increment cooking count
                ++cookingCount;

                return markUpdated();
            }
        }

        return false;
    }
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        assert level != null;
        Containers.dropContents(level, worldPosition, inventory);
    }
    public boolean hasCookingSlots() {
        return  MAX_COOKING_SLOTS - cookingCount > 0;
    }

    public static void particleTick(Level level, BlockPos blockPos, BlockState state, OvenBlockEntity blockEntity) {
        Random random = level.random;
        if (random.nextFloat() < 0.11F) {
            for(int i = 0; i < random.nextInt(2) + 2; ++i) {
                CampfireBlock.makeParticles(level, blockPos, state.getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
        }

        int l = state.getValue(CampfireBlock.FACING).get2DDataValue();

        for(int j = 0; j < blockEntity.cookItems.size(); ++j) {
            if (!blockEntity.cookItems.get(j).isEmpty() && random.nextFloat() < 0.2F) {
                Direction direction = Direction.from2DDataValue(Math.floorMod(j + l, 4));
                float f = 0.3125F;
                double d0 = (double)blockPos.getX() + 0.5D - (double)((float)direction.getStepX() * 0.3125F) + (double)((float)direction.getClockWise().getStepX() * 0.3125F);
                double d1 = (double)blockPos.getY() + 0.5D;
                double d2 = (double)blockPos.getZ() + 0.5D - (double)((float)direction.getStepZ() * 0.3125F) + (double)((float)direction.getClockWise().getStepZ() * 0.3125F);

                for(int k = 0; k < 4; ++k) {
                    level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 5.0E-4D, 0.0D);
                }
            }
        }
    }
    public static void cookTick(Level level, BlockPos blockPos, BlockState state, OvenBlockEntity blockEntity) {
        boolean hasFoodItem = false;

        for(int i = 0; i < blockEntity.cookItems.size(); ++i) {
            ItemStack cookItemStack = blockEntity.cookItems.get(i);

            if (!cookItemStack.isEmpty()) {
                hasFoodItem = true;
                ++blockEntity.cookingProgress[i];

                if (blockEntity.cookingProgress[i] >= blockEntity.cookingTime[i]) {
                    Container container = new SimpleContainer(cookItemStack);
                    ItemStack recipeItemStack = level.getRecipeManager()
                            .getRecipeFor(RecipeType.CAMPFIRE_COOKING, container, level)
                            .map((recipe) -> recipe.assemble(container)).orElse(cookItemStack);

                    //  cooking completed - drop item
                    Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), recipeItemStack);
                    //  decrement cooking count
                    --blockEntity.cookingCount;

                    //  no longer cooking? set cook property to false...
                    if(blockEntity.cookingCount == 0) {
                        state.setValue(ClayOvenBlock.COOK, false);
                    }

                    blockEntity.cookItems.set(i, ItemStack.EMPTY);
                    level.sendBlockUpdated(blockPos, state, state, 3);
                }
            }
        }

        AbstractFurnaceBlockEntity.serverTick(level, blockPos, state, blockEntity);

        if (hasFoodItem) {
            setChanged(level, blockPos, state);
        }
    }

    private boolean markUpdated() {
        setChanged();
        Level level;

        if((level = getLevel()) != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);

            return true;
        }
        else {
            return false;
        }
    }
}
