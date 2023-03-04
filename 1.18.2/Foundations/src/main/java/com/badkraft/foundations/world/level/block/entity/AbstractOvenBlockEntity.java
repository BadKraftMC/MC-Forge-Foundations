package com.badkraft.foundations.world.level.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static net.minecraftforge.common.ForgeHooks.getBurnTime;

public abstract class AbstractOvenBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {
    protected static final int SLOT_INPUT = 0;
    protected static final int SLOT_FUEL = 1;
    protected static final int SLOT_RESULT = 2;
    public static final int DATA_LIT_TIME = 0;
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};
    public static final int DATA_LIT_DURATION = 1;
    public static final int DATA_COOKING_PROGRESS = 2;
    public static final int DATA_COOKING_TOTAL_TIME = 3;
    public static final int NUM_DATA_VALUES = 4;
    public static final int BURN_TIME_STANDARD = 200;
    public static final int BURN_COOL_SPEED = 2;
    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    int litTime;
    int litDuration;
    int cookingProgress;
    int cookingTotalTime;
    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int id) {
            return switch (id) {
                case 0 -> AbstractOvenBlockEntity.this.litTime;
                case 1 -> AbstractOvenBlockEntity.this.litDuration;
                case 2 -> AbstractOvenBlockEntity.this.cookingProgress;
                case 3 -> AbstractOvenBlockEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        public void set(int id, int value) {
            switch (id) {
                case 0 -> AbstractOvenBlockEntity.this.litTime = value;
                case 1 -> AbstractOvenBlockEntity.this.litDuration = value;
                case 2 -> AbstractOvenBlockEntity.this.cookingProgress = value;
                case 3 -> AbstractOvenBlockEntity.this.cookingTotalTime = value;
            }

        }

        public int getCount() {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;

    protected AbstractOvenBlockEntity(BlockEntityType<?> entityType, BlockPos blockPos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(entityType, blockPos, state);
        this.recipeType = recipeType;
    }

/*
 @Deprecated
    public static Map<Item, Integer> getFuel() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        add(map, Items.LAVA_BUCKET, 20000);
        add(map, Blocks.COAL_BLOCK, 16000);
        add(map, Items.BLAZE_ROD, 2400);
        add(map, Items.COAL, 1600);
        add(map, Items.CHARCOAL, 1600);
        add(map, ItemTags.LOGS, 300);
        add(map, ItemTags.PLANKS, 300);
        add(map, ItemTags.WOODEN_STAIRS, 300);
        add(map, ItemTags.WOODEN_SLABS, 150);
        add(map, ItemTags.WOODEN_TRAPDOORS, 300);
        add(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        add(map, Blocks.OAK_FENCE, 300);
        add(map, Blocks.BIRCH_FENCE, 300);
        add(map, Blocks.SPRUCE_FENCE, 300);
        add(map, Blocks.JUNGLE_FENCE, 300);
        add(map, Blocks.DARK_OAK_FENCE, 300);
        add(map, Blocks.ACACIA_FENCE, 300);
        add(map, Blocks.OAK_FENCE_GATE, 300);
        add(map, Blocks.BIRCH_FENCE_GATE, 300);
        add(map, Blocks.SPRUCE_FENCE_GATE, 300);
        add(map, Blocks.JUNGLE_FENCE_GATE, 300);
        add(map, Blocks.DARK_OAK_FENCE_GATE, 300);
        add(map, Blocks.ACACIA_FENCE_GATE, 300);
        add(map, Blocks.NOTE_BLOCK, 300);
        add(map, Blocks.BOOKSHELF, 300);
        add(map, Blocks.LECTERN, 300);
        add(map, Blocks.JUKEBOX, 300);
        add(map, Blocks.CHEST, 300);
        add(map, Blocks.TRAPPED_CHEST, 300);
        add(map, Blocks.CRAFTING_TABLE, 300);
        add(map, Blocks.DAYLIGHT_DETECTOR, 300);
        add(map, ItemTags.BANNERS, 300);
        add(map, Items.BOW, 300);
        add(map, Items.FISHING_ROD, 300);
        add(map, Blocks.LADDER, 300);
        add(map, ItemTags.SIGNS, 200);
        add(map, Items.WOODEN_SHOVEL, 200);
        add(map, Items.WOODEN_SWORD, 200);
        add(map, Items.WOODEN_HOE, 200);
        add(map, Items.WOODEN_AXE, 200);
        add(map, Items.WOODEN_PICKAXE, 200);
        add(map, ItemTags.WOODEN_DOORS, 200);
        add(map, ItemTags.BOATS, 1200);
        add(map, ItemTags.WOOL, 100);
        add(map, ItemTags.WOODEN_BUTTONS, 100);
        add(map, Items.STICK, 100);
        add(map, ItemTags.SAPLINGS, 100);
        add(map, Items.BOWL, 100);
        add(map, ItemTags.CARPETS, 67);
        add(map, Blocks.DRIED_KELP_BLOCK, 4001);
        add(map, Items.CROSSBOW, 300);
        add(map, Blocks.BAMBOO, 50);
        add(map, Blocks.DEAD_BUSH, 100);
        add(map, Blocks.SCAFFOLDING, 400);
        add(map, Blocks.LOOM, 300);
        add(map, Blocks.BARREL, 300);
        add(map, Blocks.CARTOGRAPHY_TABLE, 300);
        add(map, Blocks.FLETCHING_TABLE, 300);
        add(map, Blocks.SMITHING_TABLE, 300);
        add(map, Blocks.COMPOSTER, 300);
        add(map, Blocks.AZALEA, 100);
        add(map, Blocks.FLOWERING_AZALEA, 100);
        return map;
    }
    private static void add(Map<Item, Integer> itemMap, TagKey<Item> itemTag, int value) {
        for(Holder<Item> holder : Registry.ITEM.getTagOrEmpty(itemTag)) {
            if (!isNeverAFurnaceFuel(holder.value())) {
                itemMap.put(holder.value(), value);
            }
        }

    }
    private static void add(Map<Item, Integer> p_58375_, ItemLike p_58376_, int p_58377_) {
        Item item = p_58376_.asItem();
        if (isNeverAFurnaceFuel(item)) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw (IllegalStateException) Util.pauseInIde(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item.getName((ItemStack)null).getString() + " a furnace fuel. That will not work!"));
            }
        } else {
            p_58375_.put(item, p_58377_);
        }
    }
*/

    @SuppressWarnings("deprecation")
    private static boolean isNeverAFurnaceFuel(Item item) {
        return item.builtInRegistryHolder().is(ItemTags.NON_FLAMMABLE_WOOD);
    }

    protected boolean isSmelting() {
        return this.litTime > 0;
    }

    public void load(CompoundTag p_155025_) {
        super.load(p_155025_);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(p_155025_, this.items);
        this.litTime = p_155025_.getInt("BurnTime");
        this.cookingProgress = p_155025_.getInt("CookTime");
        this.cookingTotalTime = p_155025_.getInt("CookTimeTotal");
        this.litDuration = this.getBurnDuration(this.items.get(1));
        CompoundTag compoundtag = p_155025_.getCompound("RecipesUsed");

        for(String s : compoundtag.getAllKeys()) {
            this.recipesUsed.put(new ResourceLocation(s), compoundtag.getInt(s));
        }

    }

    protected void saveAdditional(CompoundTag p_187452_) {
        super.saveAdditional(p_187452_);
        p_187452_.putInt("BurnTime", this.litTime);
        p_187452_.putInt("CookTime", this.cookingProgress);
        p_187452_.putInt("CookTimeTotal", this.cookingTotalTime);
        ContainerHelper.saveAllItems(p_187452_, this.items);
        CompoundTag compoundtag = new CompoundTag();
        this.recipesUsed.forEach((p_187449_, p_187450_) -> {
            compoundtag.putInt(p_187449_.toString(), p_187450_);
        });
        p_187452_.put("RecipesUsed", compoundtag);
    }

    public static void smeltTick(Level level, BlockPos blockPos, BlockState state, AbstractOvenBlockEntity oven) {
        boolean isSmelting = oven.isSmelting();
        boolean hasIngredients = false;
        if (oven.isSmelting()) {
            --oven.litTime;
        }

        ItemStack itemstack = oven.items.get(1);
        if (oven.isSmelting() || !itemstack.isEmpty() && !oven.items.get(0).isEmpty()) {
            Recipe<?> recipe = level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>)oven.recipeType, oven, level).orElse(null);
            int i = oven.getMaxStackSize();
            if (!oven.isSmelting() && oven.canBurn(recipe, oven.items, i)) {
                oven.litTime = oven.getBurnDuration(itemstack);
                oven.litDuration = oven.litTime;
                if (oven.isSmelting()) {
                    hasIngredients = true;
                    if (itemstack.hasContainerItem())
                        oven.items.set(1, itemstack.getContainerItem());
                    else
                    if (!itemstack.isEmpty()) {
                        Item item = itemstack.getItem();
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            oven.items.set(1, itemstack.getContainerItem());
                        }
                    }
                }
            }

            if (oven.isSmelting() && oven.canBurn(recipe, oven.items, i)) {
                ++oven.cookingProgress;
                if (oven.cookingProgress == oven.cookingTotalTime) {
                    oven.cookingProgress = 0;
                    oven.cookingTotalTime = getTotalCookTime(level, oven.recipeType, oven);
                    if (oven.burn(recipe, oven.items, i)) {
                        oven.setRecipeUsed(recipe);
                    }

                    hasIngredients = true;
                }
            } else {
                oven.cookingProgress = 0;
            }
        } else if (!oven.isSmelting() && oven.cookingProgress > 0) {
            oven.cookingProgress = Mth.clamp(oven.cookingProgress - 2, 0, oven.cookingTotalTime);
        }

        if (isSmelting != oven.isSmelting()) {
            hasIngredients = true;
            state = state.setValue(AbstractFurnaceBlock.LIT, Boolean.valueOf(oven.isSmelting()));
            level.setBlock(blockPos, state, 3);
        }

        if (hasIngredients) {
            setChanged(level, blockPos, state);
        }

    }

    private boolean canBurn(@Nullable Recipe<?> recipe, NonNullList<ItemStack> itemStack, int count) {
        if (!itemStack.get(0).isEmpty() && recipe != null) {
            ItemStack itemstack = ((Recipe<WorldlyContainer>) recipe).assemble(this);
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack smeltstack = itemStack.get(2);
                if (smeltstack.isEmpty()) {
                    return true;
                } else if (!smeltstack.sameItem(itemstack)) {
                    return false;
                } else if (smeltstack.getCount() + itemstack.getCount() <= count && smeltstack.getCount() + itemstack.getCount() <= smeltstack.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
                    return true;
                } else {
                    return smeltstack.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

    private boolean burn(@Nullable Recipe<?> p_155027_, NonNullList<ItemStack> p_155028_, int p_155029_) {
        if (p_155027_ != null && this.canBurn(p_155027_, p_155028_, p_155029_)) {
            ItemStack itemstack = p_155028_.get(0);
            ItemStack itemstack1 = ((Recipe<WorldlyContainer>) p_155027_).assemble(this);
            ItemStack itemstack2 = p_155028_.get(2);
            if (itemstack2.isEmpty()) {
                p_155028_.set(2, itemstack1.copy());
            } else if (itemstack2.is(itemstack1.getItem())) {
                itemstack2.grow(itemstack1.getCount());
            }

            if (itemstack.is(Blocks.WET_SPONGE.asItem()) && !p_155028_.get(1).isEmpty() && p_155028_.get(1).is(Items.BUCKET)) {
                p_155028_.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    protected int getBurnDuration(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        } else {
            Item item = itemStack.getItem();
            return getBurnTime(itemStack, this.recipeType);
        }
    }

    private static int getTotalCookTime(Level level, RecipeType<? extends AbstractCookingRecipe> recipeType, Container container) {
        return level.getRecipeManager().getRecipeFor((RecipeType<AbstractCookingRecipe>)recipeType, container, level).map(AbstractCookingRecipe::getCookingTime).orElse(200);
    }

    public static boolean isFuel(ItemStack itemStack) {
        return getBurnTime(itemStack, null) > 0;
    }

    public int[] getSlotsForFace(Direction direction) {
        if (direction == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
        }
    }

    public boolean canPlaceItemThroughFace(int slotId, ItemStack itemStack, @Nullable Direction direction) {
        return this.canPlaceItem(slotId, itemStack);
    }

    public boolean canTakeItemThroughFace(int slotId, ItemStack itemStack, Direction direction) {
        if (direction == Direction.DOWN && slotId == 1) {
            return itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET);
        } else {
            return true;
        }
    }

    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public ItemStack getItem(int slotId) {
        return items.get(slotId);
    }

    public ItemStack removeItem(int slotId, int count) {
        return ContainerHelper.removeItem(items, slotId, count);
    }

    public ItemStack removeItemNoUpdate(int slotId) {
        return ContainerHelper.takeItem(items, slotId);
    }

    public void setItem(int slotId, ItemStack itemStack) {
        ItemStack itemstack = this.items.get(slotId);
        boolean flag = !itemStack.isEmpty() && itemStack.sameItem(itemstack) && ItemStack.tagMatches(itemStack, itemstack);
        this.items.set(slotId, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }

        if (slotId == 0 && !flag) {
            this.cookingTotalTime = getTotalCookTime(this.level, this.recipeType, this);
            this.cookingProgress = 0;
            this.setChanged();
        }

    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    public boolean canPlaceItem(int slotId, ItemStack itemStack) {
        if (slotId == 2) {
            return false;
        } else if (slotId != 1) {
            return true;
        } else {
            ItemStack itemstack = this.items.get(1);
            return getBurnTime(itemStack, this.recipeType) > 0 || itemStack.is(Items.BUCKET) && !itemstack.is(Items.BUCKET);
        }
    }

    public void clearContent() {
        this.items.clear();
    }

    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation resourcelocation = recipe.getId();
            this.recipesUsed.addTo(resourcelocation, 1);
        }

    }

    @Nullable
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    public void awardUsedRecipes(Player player) {
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer p_155004_) {
        List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(p_155004_.getLevel(), p_155004_.position());
        p_155004_.awardRecipes(list);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel serverLevel, Vec3 vec3) {
        List<Recipe<?>> list = Lists.newArrayList();

        for(Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            serverLevel.getRecipeManager().byKey(entry.getKey()).ifPresent((p_155023_) -> {
                list.add(p_155023_);
                createExperience(serverLevel, vec3, entry.getIntValue(), ((AbstractCookingRecipe)p_155023_).getExperience());
            });
        }

        return list;
    }

    private static void createExperience(ServerLevel serverLevel, Vec3 vec3, int x, float v) {
        int i = Mth.floor((float)x * v);
        float f = Mth.frac((float)x * v);
        if (f != 0.0F && Math.random() < (double)f) {
            ++i;
        }

        ExperienceOrb.award(serverLevel, vec3, i);
    }

    public void fillStackedContents(StackedContents stack) {
        for(ItemStack itemstack : this.items) {
            stack.accountStack(itemstack);
        }

    }

    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
            net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == Direction.UP)
                return handlers[0].cast();
            else if (facing == Direction.DOWN)
                return handlers[1].cast();
            else
                return handlers[2].cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (int x = 0; x < handlers.length; x++)
            handlers[x].invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.handlers = net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    }
}
