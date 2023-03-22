package com.badkraft.foundations.world.item;

import com.badkraft.foundations.Foundations;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;


public class ModItems {
    //	deferred items register (deferred same as lazy?)
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Foundations.MOD_ID);

    //	register items
    public static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> getItem){
        return ITEMS.register(name, getItem);
    }

    public static void register(IEventBus eventBus) { ITEMS.register(eventBus); }

    //	tab icon
    public static final RegistryObject<Item> MEDALLION = register("foundations_medallion", MedallionItem::new);

    //	items
    public static final RegistryObject<Item> CLAY_ORE = register("clay_ore",
            getItem(CreativeModeTab.TAB_MATERIALS));
    public static final RegistryObject<Item> CLAY_BRICK = register("clay_brick",
            getItem(CreativeModeTab.TAB_MATERIALS));
    public static final RegistryObject<Item> COPPER_NUGGET_ORE = register("copper_nugget_ore",
            getItem(CreativeModeTab.TAB_MATERIALS));
    public static final RegistryObject<Item> COPPER_NUGGET = register("copper_nugget",
            getItem(CreativeModeTab.TAB_MATERIALS));
    public static final RegistryObject<Item> IRON_NUGGET_ORE = register("iron_nugget_ore",
            getItem(CreativeModeTab.TAB_MATERIALS));
    public static final RegistryObject<Item> GOLD_NUGGET_ORE = register("gold_nugget_ore",
            getItem(CreativeModeTab.TAB_MATERIALS));
    public static final RegistryObject<Item> ROUGH_STONE_ROCK = register("rough_stone_rock",
            getItem(CreativeModeTab.TAB_MATERIALS));
    public static final RegistryObject<Item> MASONRY_MORTAR = register("masonry_mortar",
            getItem(CreativeModeTab.TAB_MATERIALS));

    //	tools
    public static final RegistryObject<Item> IRON_MASONRY_CHISEL = register("iron_masonry_chisel", () ->
            new MasonryChiselItem(Tiers.IRON, 32));
    public static final RegistryObject<Item> COPPER_MASONRY_CHISEL = register("copper_masonry_chisel", () ->
            new MasonryChiselItem(ItemTiers.COPPER, 22));
    public static final RegistryObject<Item> FLINT_AXE = register("flint_axe", FlintAxeItem::new);
    public static final RegistryObject<Item> FLINT_SHOVEL = register("flint_shovel", FlintShovelItem::new);
    public static final RegistryObject<Item> FLINT_HOE = register("flint_hoe", FlintHoeItem::new);

    //	gets a generic Item and puts it on the supplied tab
    private static <T extends Item> Supplier<Item> getItem(CreativeModeTab creativeTab) {
        // TODO Auto-generated method stub
        return () -> new Item(new Item.Properties().tab(creativeTab));
    }

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
}
