package com.badkraft.foundations.world.inventory;


import com.badkraft.foundations.world.inventory.slot.BenchInventorySlot;
import com.badkraft.foundations.world.inventory.slot.BenchResultSlot;
import com.badkraft.foundations.world.level.block.entity.BenchBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;


@SuppressWarnings("unchecked")
public abstract class AbstractBenchMenu extends AbstractContainerMenu {
    protected static final Logger LOGGER = LogUtils.getLogger();

/*     CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
     must assign a slot number to each of the slots used by the GUI.
     For this container, we can see both the tile inventory's slots and the player inventory slots and the hot bar.
     Each time we add a Slot to the container, it automatically increases the slotIndex, which means
      0 - 8 = hot bar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
      9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
      36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
*/
    private static final int HOT_BAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOT_BAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = BenchBlockEntity.MAX_BENCH_SLOTS;  // must be the number of slots you have!

    private final BenchBlockEntity benchEntity;
    private final ContainerLevelAccess access;

    private final DataSlot selectedRecipeIndex = DataSlot.standalone();

    protected final Level level;

    protected AbstractBenchMenu(MenuType<?> menuType, int containerId, Inventory inventory, BlockEntity blockEntity) {
        super(menuType, containerId);

        access = ContainerLevelAccess.NULL;
        level = inventory.player.level;
        //  ASSUMPTION: blockEntity is instance of BenchBlockEntity
        benchEntity = ((BenchBlockEntity) blockEntity);

        checkContainerSize(inventory, TE_INVENTORY_SLOT_COUNT);

        addPlayerInventory(inventory);
        addPlayerHotBar(inventory);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;  //EMPTY_ITEM
        }

        LOGGER.debug("[ABSTRACT_BENCH_MENU] :: quickMoveStack [" + index + "]");

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, benchEntity.getBlockPos()),
                player, getBlock());
    }

    protected <T extends BenchBlockEntity> T getBenchEntity() {
        return  (T)benchEntity;
    }
    protected abstract Block getBlock();
    protected void setSlotHandlers(IItemHandler handler) {
        addBenchSlots(handler);
        addCompulsorySlots(handler);
        addResultSlot(handler);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int idx = 0; idx < 9; ++idx) {
                addSlot(new Slot(playerInventory, row * 9 + idx + 9, 8 + idx * 18, 84 + row * 18));
            }
        }
    }
    private void addPlayerHotBar(Inventory playerInventory) {
        for (int idx = 0; idx < 9; ++idx) {
            addSlot(new Slot(playerInventory, idx, idx * 18 + 8, 142));
        }
    }
    private void addBenchSlots(IItemHandler itemHandler) {
        LOGGER.debug("[ABSTRACT_BENCH_MENU] :: addBenchInventory");

        int rowWidth = 2;
        for (int idx = BenchBlockEntity.INVENTORY_SLOTS_START; idx < BenchBlockEntity.INVENTORY_SLOTS_END; ++idx) {
            int row = idx / rowWidth;
            int col = idx % rowWidth;

            int x = col * 18 + 8;
            int y = row * 2 + row * 16 + 17;

            LOGGER.debug("[ABSTRACT_BENCH_MENU] :: ADDING BenchInventorySlot[" + idx + "] (" + row + "," + col + ") (" + x + "," + y + ")");

            addSlot(new BenchInventorySlot(itemHandler, idx, x, y));
        }
    }
    private void addCompulsorySlots(IItemHandler itemHandler) {
        addSlot(new BenchInventorySlot(itemHandler, BenchBlockEntity.COMPULSORY_SLOT_1, 134, 53));
        addSlot(new BenchInventorySlot(itemHandler, BenchBlockEntity.COMPULSORY_SLOT_2, 152, 53));
    }
    private void addResultSlot(IItemHandler itemHandler) {
        addSlot(new BenchResultSlot(itemHandler, BenchBlockEntity.BENCH_RESULT_SLOT, 148, 24));
    }
}
