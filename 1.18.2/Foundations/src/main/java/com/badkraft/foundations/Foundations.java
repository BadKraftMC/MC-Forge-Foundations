package com.badkraft.foundations;

import com.badkraft.foundations.init.BlockInit;
import com.badkraft.foundations.init.ItemInit;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.slf4j.Logger;


@Mod("foundations")
public class Foundations {
    private static final Logger LOGGER = LogUtils.getLogger();

	private ItemInit itemInit;

	public static final String MOD_ID = "foundations";
	static Foundations INSTANCE;

	public static final CreativeModeTab FOUNDATIONS_TAB	 = new FoundationsTab(MOD_ID);
	
	public Foundations() {
		INSTANCE = this;
		LOGGER.info("*** *** Welcome to FOUNDATIONS!!! *** ***");
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		ItemInit.ITEMS.register(bus);
		BlockInit.BLOCKS.register(bus);

		MinecraftForge.EVENT_BUS.register(this);
	}

	final void onFoundationsInitialized() {
		itemInit = new ItemInit();
		itemInit.removeItems();
	}

	//	Foundations Category (Tab)
	private static class FoundationsTab extends CreativeModeTab {

		public FoundationsTab(String tabName) {
			super("%stab".formatted(tabName));
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(ItemInit.MEDALLION.get());
		}	
	}

}
