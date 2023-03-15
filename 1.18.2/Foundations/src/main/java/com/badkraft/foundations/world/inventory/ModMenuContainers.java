package com.badkraft.foundations.world.inventory;

import com.badkraft.foundations.Foundations;
import com.mojang.logging.LogUtils;
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
    private static Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, Foundations.MOD_ID);

    public static final RegistryObject<MenuType<StoneMasonCraftingMenu>> STONE_CRAFTING_MENU =
            getMenu("stone_crafting", StoneMasonCraftingMenu::new);

    public static void register(IEventBus eventBus) { MENU_TYPES.register(eventBus); }

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> getMenu(String name, IContainerFactory<T> factory) {
        return MENU_TYPES.register(name, () -> IForgeMenuType.create(factory));
    }
}
