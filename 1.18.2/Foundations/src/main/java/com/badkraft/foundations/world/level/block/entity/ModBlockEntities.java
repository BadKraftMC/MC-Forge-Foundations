package com.badkraft.foundations.world.level.block.entity;

import com.badkraft.foundations.Foundations;
import com.badkraft.foundations.world.level.block.ModBlocks;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;


public class ModBlockEntities {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Foundations.MOD_ID);

    public static final RegistryObject<BlockEntityType<OvenBlockEntity>> OVEN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("oven_block_entity", () ->
                    BlockEntityType.Builder.of(OvenBlockEntity::new,
                            ModBlocks.CLAY_OVEN.get()).build(null));
    public static final RegistryObject<BlockEntityType<MasonryBenchBlockEntity>> MASONRY_BENCH_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("masonry_bench_block_entity", () ->
                    BlockEntityType.Builder.of(MasonryBenchBlockEntity::new,
                            ModBlocks.MASONRY_BENCH.get()).build(null));

/*        public static final RegistryObject<BlockEntityType<CarpenterBenchBlocEntity>> CARPENTER_BENCH_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("carpenter_bench_entity", () ->
                    BlockEntityType.Builder.of(CarpenterBenchBlockEntity::new,
                            ModBlocks.CARPENTER_BENCH.get()).build(null));*/

    public static void register(IEventBus eventBus) { BLOCK_ENTITIES.register(eventBus); }

}
