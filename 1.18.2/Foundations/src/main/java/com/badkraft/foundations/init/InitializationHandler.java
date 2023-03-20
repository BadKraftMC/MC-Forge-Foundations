package com.badkraft.foundations.init;

import com.badkraft.foundations.world.inventory.ModMenuContainers;
import com.badkraft.foundations.world.inventory.screen.MasonryBenchScreen;
import com.badkraft.foundations.world.item.ItemEditor;

import com.badkraft.foundations.Foundations;
import com.badkraft.foundations.world.level.block.ModBlocks;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;


@Mod.EventBusSubscriber(modid = Foundations.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class InitializationHandler {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Foundations FOUNDATIONS = Foundations.INSTANCE;

	@SubscribeEvent
	public static void onInitializedEvent(FMLCommonSetupEvent event) {
		LOGGER.debug("*** *** MC FOUNDATIONS INITIALIZED *** ***");

		ItemEditor.editItems();
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event){
		LOGGER.debug("*** *** MC FOUNDATIONS CLIENT SETUP *** ***");

		ItemBlockRenderTypes.setRenderLayer(ModBlocks.CLAY_OVEN.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.MASONRY_BENCH.get(), RenderType.solid());

		MenuScreens.register(ModMenuContainers.MASONRY_CRAFTING_MENU.get(), MasonryBenchScreen::new);
	}
}
