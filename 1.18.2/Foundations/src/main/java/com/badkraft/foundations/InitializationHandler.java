package com.badkraft.foundations;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;


@Mod.EventBusSubscriber(modid = Foundations.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class InitializationHandler {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Foundations FOUNDATIONS = Foundations.INSTANCE;

	@SubscribeEvent
	public static void onInitializedEvent(FMLCommonSetupEvent event) {
		LOGGER.debug("*** *** MC FOUNDATIONS INITIALIZED *** ***");
		FOUNDATIONS.onFoundationsInitialized();
	}
}
