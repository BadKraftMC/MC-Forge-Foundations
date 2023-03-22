package com.badkraft.foundations.world.inventory;

import com.badkraft.foundations.Foundations;
import com.badkraft.foundations.world.level.block.entity.AbstractBenchBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;


public class ModMenuContainers {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final IContainerFactory<MasonryBenchMenu> getMasonryContainerFactory = (containerId, inventory, extraData) -> {
        BlockPos blockPos = null;
        AbstractBenchBlockEntity benchEntity = null;

        if (extraData != null) {
            blockPos = extraData.readBlockPos();
        }
        if (blockPos != null) {
            benchEntity = (AbstractBenchBlockEntity) inventory.player.level.getBlockEntity(blockPos);
        }

        LOGGER.debug("[MOD_MENU_CONTAINERS] :: create MasonryBenchMenu(container=" + containerId + ", hasInventory[" + !(inventory == null) + "], hasBenchEntity[" + !(benchEntity == null) + "])");

        return new MasonryBenchMenu(containerId, inventory, benchEntity);
    };

    private static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, Foundations.MOD_ID);

    public static final RegistryObject<MenuType<MasonryBenchMenu>> MASONRY_CRAFTING_MENU =
            getMenu("masonry_crafting_gui", ModMenuContainers.getMasonryContainerFactory);

    public static void register(IEventBus eventBus) { MENU_TYPES.register(eventBus); }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> getMenu(String name, IContainerFactory<T> factory) {
        return MENU_TYPES.register(name, () -> IForgeMenuType.create(factory));
    }
}
