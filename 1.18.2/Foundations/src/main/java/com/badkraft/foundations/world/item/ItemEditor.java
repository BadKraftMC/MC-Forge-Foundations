package com.badkraft.foundations.world.item;

import com.badkraft.foundations.core.ItemReflector;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.*;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ItemEditor {
	private static final Logger LOGGER = LogUtils.getLogger();

	private final Iterable<Item> removables;
	private final ItemReflector reflector;

	public static void editItems(){
		//	TODO: clean up so this is not an instantiated class...
		new ItemEditor().removeItems();
	}

	private ItemEditor() {
		reflector = new ItemReflector();
		List<Item> items = getRemovables(Arrays.asList("stone_axe", "stone_hoe", "stone_pickaxe", "stone_shovel", "stone_sword",
				"wooden_axe", "wooden_hoe", "wooden_pickaxe", "wooden_sword"));

		LOGGER.debug("Removables: [" + items.size() + "]");

		removables = items;
	}

	private void removeItems() {
		String itemName;
		String categoryName;

		for (Item item:removables) {
			itemName = item.getName(new ItemStack(item)).getString();
			assert item.getItemCategory() != null;

			categoryName = item.getItemCategory().getDisplayName().getString();
			reflector.setItemCategory(item, null);

			LOGGER.debug("Remove Item (" + itemName + ") [" + categoryName + "] [" + (item.getItemCategory() == null) + "]");

			/*
				If item.getCreativeTabs().size() is checked, a `1` is returned because the collection contains 1 null element!
			 */
		}
	}

	private static List<Item> getRemovables(List<String> removables) {
		List<Item> items = new ArrayList<>();
		for(String ri:removables){
			switch (ri){
				case "stone_axe":
					if (ModItems.STONE_AXE != null){
						items.add(ModItems.STONE_AXE);
					}

					break;
				case "stone_hoe":
					if (ModItems.STONE_HOE != null){
						items.add(ModItems.STONE_HOE);
					}

					break;
				case "stone_pickaxe":
					if (ModItems.STONE_PICK != null){
						items.add(ModItems.STONE_PICK);
					}

					break;
				case "stone_shovel":
					if (ModItems.STONE_SHOVEL != null){
						items.add(ModItems.STONE_SHOVEL);
					}

					break;
				case "stone_sword":
					if (ModItems.STONE_SWORD != null){
						items.add(ModItems.STONE_SWORD);
					}

					break;
				case "wooden_axe":
					if (ModItems.WOODEN_AXE != null){
						items.add(ModItems.WOODEN_AXE);
					}

					break;
				case "wooden_hoe":
					if (ModItems.WOODEN_HOE != null){
						items.add(ModItems.WOODEN_HOE);
					}

					break;
				case "wooden_pickaxe":
					if (ModItems.WOODEN_PICK != null){
						items.add(ModItems.WOODEN_PICK);
					}

					break;
				case "wooden_sword":
					if (ModItems.WOODEN_SWORD != null){
						items.add(ModItems.WOODEN_SWORD);
					}

					break;
				default:
					//	NOT ONE OF THE LIST SO IGNORE
					break;
			}
		}

		return items;
	}
}
