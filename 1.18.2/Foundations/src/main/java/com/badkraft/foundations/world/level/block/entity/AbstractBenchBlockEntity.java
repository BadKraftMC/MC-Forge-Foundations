package com.badkraft.foundations.world.level.block.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.annotation.Nonnull;


public abstract class AbstractBenchBlockEntity extends BlockEntity implements MenuProvider {
    protected static final Logger LOGGER = LogUtils.getLogger();

    //  bench slot counts
    public static final int INVENTORY_SLOTS_START = 0;
    public static final int INVENTORY_SLOTS_END = 6;
    public static final int COMPULSORY_SLOT_1 = 6;
    public static final int COMPULSORY_SLOT_2 = 7;
    public static final int MAX_COMPULSORY_SLOTS = 2;
    public static final int BENCH_RESULT_SLOT = 8;
    public static final int MAX_RESULT_SLOTS = 1;
    public static final int MAX_STORAGE_SLOTS = INVENTORY_SLOTS_END - INVENTORY_SLOTS_START;
    public static final int MAX_BENCH_SLOTS = MAX_STORAGE_SLOTS + MAX_COMPULSORY_SLOTS + MAX_RESULT_SLOTS;  //  not counting selection box

    //  how many slots do we have?
    private final ItemStackHandler itemHandler = new ItemStackHandler(MAX_BENCH_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    protected LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    protected final TextComponent displayName;

    protected AbstractBenchBlockEntity(String name, BlockEntityType<?> blockEntity, BlockPos blockPos, BlockState blockState) {
        super(blockEntity, blockPos, blockState);
        displayName = new TextComponent(name);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return displayName;
    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        assert level != null;
        Containers.dropContents(level, worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, AbstractBenchBlockEntity blockEntity) {
        blockEntity.tick(level, blockPos, blockState);
    }

    protected void tick(Level level, BlockPos blockPos, BlockState blockState) {
        //  add progress bar
        if(hasRecipe()) {
            craftItem();
        }
    }

    protected abstract void craftItem();
    protected abstract boolean hasRecipe();
}
