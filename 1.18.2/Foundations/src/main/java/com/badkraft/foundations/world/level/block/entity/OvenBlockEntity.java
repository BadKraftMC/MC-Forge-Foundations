package com.badkraft.foundations.world.level.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

import java.util.Optional;
import java.util.Random;

import static net.minecraftforge.common.ForgeHooks.getBurnTime;


public final class OvenBlockEntity extends BlockEntity implements MenuProvider {
    //  cooking
    private static final int COOK_COOL_SPEED = 2;
    private static final int MAX_COOKING_SLOTS = 4;
    //  smelting
    private static final int MAX_SMELTING_SLOTS = 3;
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_FUEL = 1;
    private static final int SLOT_RESULT = 2;
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot){
            setChanged();
        }
    };

    int litTime;
    int litDuration;
    int smeltingProgress;
    int smeltingTotalTime;
    private final ContainerData dataAccess = new ContainerData() {
        public int get(int id) {
            return switch (id) {
                case 0 -> OvenBlockEntity.this.litTime;
                case 1 -> OvenBlockEntity.this.litDuration;
                case 2 -> OvenBlockEntity.this.smeltingProgress;
                case 3 -> OvenBlockEntity.this.smeltingTotalTime;
                default -> 0;
            };
        }

        public void set(int id, int value) {
            switch (id) {
                case 0 -> OvenBlockEntity.this.litTime = value;
                case 1 -> OvenBlockEntity.this.litDuration = value;
                case 2 -> OvenBlockEntity.this.smeltingProgress = value;
                case 3 -> OvenBlockEntity.this.smeltingTotalTime = value;
            }
        }

        public int getCount() {
            return 4;
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final NonNullList<ItemStack> cookItems = NonNullList.withSize(MAX_COOKING_SLOTS, ItemStack.EMPTY);
    private final NonNullList<ItemStack> smeltItems = NonNullList.withSize(MAX_SMELTING_SLOTS, ItemStack.EMPTY);

    private final int[] cookingProgress = new int[MAX_COOKING_SLOTS];
    private final int[] cookingTime = new int[MAX_COOKING_SLOTS];
    private int cookingCount = 0;

    public OvenBlockEntity(BlockPos blockPos, BlockState state) {
        super(ModBlockEntities.OVEN_BLOCK_ENTITY.get(), blockPos, state);
        recipeType = RecipeType.SMELTING;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent("Clay Oven");
    }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory, @NotNull Player player) {
        TextComponent message = new TextComponent("You clicked on " + getDisplayName() + "!");
        player.sendMessage(message, player.getUUID());

        return null;
    }
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }
    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        int[] params;

        //  since this entity manages 2 items stacks we split up the items stack when loading
        NonNullList<ItemStack> items = NonNullList.withSize(MAX_COOKING_SLOTS + MAX_SMELTING_SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
        //  clear item stacks
        cookItems.clear();
        smeltItems.clear();
        //  split items int item stacks
        cookItems.addAll(items.subList(0, 3));
        smeltItems.addAll(items.subList(4, 6));
        //  load cooking parameters
        if (tag.contains("CookingTimes", 11)) {
            params = tag.getIntArray("CookingTimes");
            System.arraycopy(params, 0, cookingProgress, 0, Math.min(cookingTime.length, params.length));
        }
        if (tag.contains("CookingTotalTimes", 11)) {
            params = tag.getIntArray("CookingTotalTimes");
            System.arraycopy(params, 0, cookingTime, 0, Math.min(cookingTime.length, params.length));
        }
        //  load smelting parameters
        litTime = tag.getInt("BurnTime");
        smeltingProgress = tag.getInt("SmeltTime");
        smeltingTotalTime = tag.getInt("SmeltTimeTotal");
        litDuration = getBurnDuration(smeltItems.get(1));
        //  load recipes used
        CompoundTag compoundTag = tag.getCompound("RecipesUsed");
        for(String s : compoundTag.getAllKeys()) {
            recipesUsed.put(new ResourceLocation(s), compoundTag.getInt(s));
        }
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        //  since this entity manages 2 items stacks we combine items stack when saving
        NonNullList<ItemStack> items = NonNullList.withSize(MAX_COOKING_SLOTS + MAX_SMELTING_SLOTS, ItemStack.EMPTY);
        items.addAll(0, cookItems);
        items.addAll(4, smeltItems);
        ContainerHelper.saveAllItems(tag, items, true);
        //  save cooking parameters
        tag.putIntArray("CookingTimes", cookingProgress);
        tag.putIntArray("CookingTotalTimes", cookingTime);
        //  save smelting parameters
        tag.putInt("BurnTime", litTime);
        tag.putInt("SmeltTime", smeltingProgress);
        tag.putInt("SmeltTimeTotal", smeltingTotalTime);
        //  save recipes used
        CompoundTag compoundtag = new CompoundTag();
        recipesUsed.forEach((resLoc, i) -> {
            compoundtag.putInt(resLoc.toString(), i);
        });
        tag.put("RecipesUsed", compoundtag);
    }
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    public boolean hasCookingSlots() {
        return  MAX_COOKING_SLOTS - cookingCount > 0;
    }
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        assert level != null;
        Containers.dropContents(level, worldPosition, inventory);
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

                    blockEntity.cookItems.set(i, ItemStack.EMPTY);
                    level.sendBlockUpdated(blockPos, state, state, 3);
                }
            }
        }

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
    private int getBurnDuration(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        }
        else {
            Item item = itemStack.getItem();
            return getBurnTime(itemStack, recipeType);
        }
    }
}
