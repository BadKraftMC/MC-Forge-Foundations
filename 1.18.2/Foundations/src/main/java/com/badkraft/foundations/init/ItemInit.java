package com.badkraft.foundations.init;

import com.badkraft.foundations.Foundations;
import com.badkraft.foundations.core.ItemReflector;
import com.badkraft.foundations.world.item.ItemTiers;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;


public class ItemInit {
	private static final Logger LOGGER = LogUtils.getLogger();

	private final Iterable<Item> removables;
	private final ItemReflector reflector;

	//	get a handle on items we want to remove from tabs
	@ObjectHolder("minecraft:stone_axe")
	public static Item STONE_AXE = null;
	@ObjectHolder("minecraft:stone_hoe")
	public static Item STONE_HOE = null;
	@ObjectHolder("minecraft:stone_pickaxe")
	public static Item STONE_PICK = null;
	@ObjectHolder("minecraft:stone_shovel")
	public static Item STONE_SHOVEL = null;
	@ObjectHolder("minecraft:stone_sword")
	public static Item STONE_SWORD = null;
	@ObjectHolder("minecraft:wooden_axe")
	public static Item WOODEN_AXE = null;
	@ObjectHolder("minecraft:wooden_hoe")
	public static Item WOODEN_HOE = null;
	@ObjectHolder("minecraft:wooden_pickaxe")
	public static Item WOODEN_PICK = null;
	@ObjectHolder("minecraft:wooden_sword")
	public static Item WOODEN_SWORD = null;

	//	deferred items register (deferred same as lazy?)
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Foundations.MOD_ID);
	
	//	tab icon
	public static final RegistryObject<Item> MEDALLION = register("foundations_medallion", Medallion::new);
	
	//	items
	public static final RegistryObject<Item> CLAY_ORE = register("clay_ore", 
			GetItem(CreativeModeTab.TAB_MATERIALS));
	public static final RegistryObject<Item> COPPER_NUGGET_ORE = register("copper_nugget_ore",
			GetItem(CreativeModeTab.TAB_MATERIALS));
	public static final RegistryObject<Item> COPPER_NUGGET = register("copper_nugget",
			GetItem(CreativeModeTab.TAB_MATERIALS));
	public static final RegistryObject<Item> IRON_NUGGET_ORE = register("iron_nugget_ore",
			GetItem(CreativeModeTab.TAB_MATERIALS));
	public static final RegistryObject<Item> GOLD_NUGGET_ORE = register("gold_nugget_ore",
			GetItem(CreativeModeTab.TAB_MATERIALS));
	//	tools
	public static final RegistryObject<Item> FLINT_AXE = register("flint_axe", FlintAxe::new);
	public static final RegistryObject<Item> FLINT_SHOVEL = register("flint_shovel", FlintShovel::new);
	public static final RegistryObject<Item> FLINT_HOE = register("flint_hoe", FlintHoe::new);
	public ItemInit() {
		reflector = new ItemReflector();
		List<Item> items = getRemovables(Arrays.asList("stone_axe", "stone_hoe", "stone_pickaxe", "stone_shovel", "stone_sword",
				"wooden_axe", "wooden_hoe", "wooden_pickaxe", "wooden_sword"));

		LOGGER.debug("Removables: [" + items.size() + "]");

		removables = items;
	}

	public void removeItems() {
		String itemName;
		String categoryName;

		for (Item item:removables) {
			itemName = item.getName(new ItemStack(item)).getString();
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
					if (STONE_AXE != null){
						items.add(STONE_AXE);
					}

					break;
				case "stone_hoe":
					if (STONE_HOE != null){
						items.add(STONE_HOE);
					}

					break;
				case "stone_pickaxe":
					if (STONE_PICK != null){
						items.add(STONE_PICK);
					}

					break;
				case "stone_shovel":
					if (STONE_SHOVEL != null){
						items.add(STONE_SHOVEL);
					}

					break;
				case "stone_sword":
					if (STONE_SWORD != null){
						items.add(STONE_SWORD);
					}

					break;
				case "wooden_axe":
					if (WOODEN_AXE != null){
						items.add(WOODEN_AXE);
					}

					break;
				case "wooden_hoe":
					if (WOODEN_HOE != null){
						items.add(WOODEN_HOE);
					}

					break;
				case "wooden_pickaxe":
					if (WOODEN_PICK != null){
						items.add(WOODEN_PICK);
					}

					break;
				case "wooden_sword":
					if (WOODEN_SWORD != null){
						items.add(WOODEN_SWORD);
					}

					break;
				default:
					//	NOT ONE OF THE LIST SO IGNORE
					break;
			}
		}

		return items;
	}
	//	register items
	private static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> getItem){
		return ITEMS.register(name, getItem);
	}
	//	gets a generic Item and puts it on the supplied tab
	private static <T extends Item> Supplier<Item> GetItem(CreativeModeTab tab) {
		// TODO Auto-generated method stub
		return () -> new Item(new Item.Properties().tab(tab));
	}

	//	=====================================================================================
	//	item classes
	private static class Medallion extends PickaxeItem {
		private static final int atkDmgBase = 0;
		private static final float atkSpdBase = 0.0f;

		public Medallion() {
			super(ItemTiers.PERMANENT, atkDmgBase, atkSpdBase, new Item.Properties());
		}
	}
	//	tool classes
	private static class FlintAxe extends AxeItem {
		private static final int atkDmgBase = 1;
		private static final float atkSpdBase = -2.5f;

		public FlintAxe() {
			super(ItemTiers.FLINT, atkDmgBase, atkSpdBase, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
		}
	}
	private static class FlintShovel extends ShovelItem {
		private static final float atkDmgBase = 0.0f;
		private static final float atkSpdBase = 0.0f;

		public FlintShovel() {
			super(ItemTiers.FLINT, atkDmgBase, atkSpdBase, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
		}
	}
	private static class FlintHoe extends HoeItem {
		private static final int atkDmgBase = 0;
		private static final float atkSpdBase = 0.0f;

		public FlintHoe() {
			super(ItemTiers.FLINT, atkDmgBase, atkSpdBase, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
		}
	}
}
