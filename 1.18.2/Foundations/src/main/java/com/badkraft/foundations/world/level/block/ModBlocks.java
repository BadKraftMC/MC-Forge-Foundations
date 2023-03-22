package com.badkraft.foundations.world.level.block;

import com.badkraft.foundations.Foundations;
import com.badkraft.foundations.world.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
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

    private static final BlockBehaviour.Properties QUARTZ_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE, MaterialColor.QUARTZ)
            .requiresCorrectToolForDrops().strength(1.5F, 6.0F);
    private static final BlockBehaviour.Properties STONE_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE)
            .requiresCorrectToolForDrops().strength(1.5F, 6.0F);

    public static final RegistryObject<Block> CLAY_BLOCK = registerBlockItem("clay_block",
            ClayBlock::new, o -> getBlockItem(o, Foundations.FOUNDATIONS_TAB));
    public static final RegistryObject<Block> ROUGH_STONE_BLOCK = registerBlockItem("rough_stone_block",
            () -> getBlock(STONE_PROPERTIES), o -> getBlockItem(o, CreativeModeTab.TAB_BUILDING_BLOCKS));
    public static final RegistryObject<Block> ROUGH_STONE_SLAB = registerBlockItem("rough_stone_slab",
            () -> getSlab(STONE_PROPERTIES), o -> getBlockItem(o, CreativeModeTab.TAB_BUILDING_BLOCKS));
    public static final RegistryObject<Block> ROUGH_STONE_MASONRY_BLOCK = registerBlockItem("rough_stone_masonry_block",
            () -> getBlock(STONE_PROPERTIES), o -> getBlockItem(o, CreativeModeTab.TAB_BUILDING_BLOCKS));

    public static final RegistryObject<Block> CLAY_OVEN = registerBlockItem("clay_oven",
            ClayOvenBlock::new, o -> getBlockItem(o, Foundations.FOUNDATIONS_TAB));
    public static final RegistryObject<Block> MASONRY_BENCH = registerBlockItem("masonry_bench",
//            () -> getBlock(STONE_PROPERTIES), o -> getBlockItem(o, CreativeModeTab.TAB_BUILDING_BLOCKS));
            MasonryBenchBlock::new, o -> getBlockItem(o, Foundations.FOUNDATIONS_TAB));

    public static void register(IEventBus eventBus) { BLOCKS.register(eventBus); }

    //	register block
    private static <T extends Block> RegistryObject<T> registerBlock(final String name, final Supplier<? extends T> getBlock){
        return BLOCKS.register(name, getBlock);
    }
    //	register block item
    private static <T extends Block> RegistryObject<T> registerBlockItem(final String name, final Supplier<? extends T> getBlock,
                                                                         Function<RegistryObject<T>, Supplier<? extends Item>> setItem){
        RegistryObject<T> block = registerBlock(name, getBlock);
        ModItems.register(name, setItem.apply(block));

        return block;
    }

    private static <T extends Block> T getBlock(BlockBehaviour.Properties properties) {
        return (T) new Block(properties);
    }
    private static <T extends Block> T getSlab(BlockBehaviour.Properties properties) {
        return (T) new SlabBlock(properties);
    }
    //	gets a generic BlockItem and puts it on the supplied tab
    private static <T extends Item> Supplier<Item> getBlockItem(RegistryObject<Block> block, CreativeModeTab tab) {
        // TODO Auto-generated method stub
        return () -> new BlockItem(block.get(), new Item.Properties().tab(tab));
    }
    //	gets a light emission level for block state provider
    private static ToIntFunction<BlockState> litBlockEmission(int value) {
        return (blockState) -> blockState.getValue(BlockStateProperties.LIT) ? value : 0;
    }
}
