package com.badkraft.foundations.init;

import java.util.function.Function;
import java.util.function.Supplier;

import com.badkraft.foundations.Foundations;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class BlockInit {
	//	deferred block register
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Foundations.MOD_ID);
	//	reference ItemInit deferred register
	public static final DeferredRegister<Item> ITEMS = ItemInit.ITEMS;

	//	blocks
	public static final RegistryObject<Block> CLAY_BLOCK = registerBlockItem("clay_block",
			ClayBlock::new, o -> GetBlockItem(o, Foundations.FOUNDATIONS_TAB));
	
	//	register blocks
	private static <T extends Block> RegistryObject<T> registerBlock(final String name, final Supplier<? extends T> getBlock){
		return BLOCKS.register(name, getBlock);
	}
	//	register block items
	private static <T extends Block> RegistryObject<T> registerBlockItem(final String name, final Supplier<? extends T> getBlock,
			Function<RegistryObject<T>, Supplier<? extends Item>> setItem){
		RegistryObject<T> block = registerBlock(name, getBlock);
		ITEMS.register(name, setItem.apply(block));
		
		return block;
	}

	//	gets a generic BlockItem and puts it on the supplied tab
	private static <T extends Item> Supplier<Item> GetBlockItem(RegistryObject<Block> block, CreativeModeTab tab) {
		// TODO Auto-generated method stub
		return () -> new BlockItem(block.get(), new Item.Properties().tab(tab));
	}
	
	//	=====================================================================================
	//	block classes
	private static class ClayBlock extends Block {
		public ClayBlock() {
			super(BlockBehaviour.Properties
					.of(Material.CLAY, MaterialColor.COLOR_GRAY)
					.strength(0.5f)
					.sound(SoundType.GRAVEL));
		}
	}
}
