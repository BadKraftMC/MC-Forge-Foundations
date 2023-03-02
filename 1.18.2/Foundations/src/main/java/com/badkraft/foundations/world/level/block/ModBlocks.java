package com.badkraft.foundations.world.level.block;

import com.badkraft.foundations.Foundations;
import com.badkraft.foundations.world.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;


public class ModBlocks {
    //	deferred block register
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Foundations.MOD_ID);
    //	reference ItemInit deferred register

    public static final RegistryObject<Block> CLAY_BLOCK = registerBlockItem("clay_block",
            ClayBlock::new, o -> getBlockItem(o, Foundations.FOUNDATIONS_TAB));
    public static final RegistryObject<Block> CLAY_OVEN = registerBlockItem("clay_oven",
            ClayOvenBlock::new, o -> getBlockItem(o, Foundations.FOUNDATIONS_TAB));

    public static void register(IEventBus eventBus) { BLOCKS.register(eventBus); }

    //	gets a generic BlockItem and puts it on the supplied tab
    private static <T extends Item> Supplier<Item> getBlockItem(RegistryObject<Block> block, CreativeModeTab tab) {
        // TODO Auto-generated method stub
        return () -> new BlockItem(block.get(), new Item.Properties().tab(tab));
    }
    //	gets a light emission level for block state provider
    private static ToIntFunction<BlockState> litBlockEmission(int value) {
        return (blockState) -> blockState.getValue(BlockStateProperties.LIT) ? value : 0;
    }

    //	register blocks
    private static <T extends Block> RegistryObject<T> registerBlock(final String name, final Supplier<? extends T> getBlock){
        return BLOCKS.register(name, getBlock);
    }
    //	register block items
    private static <T extends Block> RegistryObject<T> registerBlockItem(final String name, final Supplier<? extends T> getBlock,
                                                                        Function<RegistryObject<T>, Supplier<? extends Item>> setItem){
        RegistryObject<T> block = registerBlock(name, getBlock);
        ModItems.register(name, setItem.apply(block));

        return block;
    }
}
