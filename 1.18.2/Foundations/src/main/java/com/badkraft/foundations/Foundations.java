package com.badkraft.foundations;

import com.badkraft.foundations.world.inventory.ModMenuContainers;
import com.badkraft.foundations.world.item.ModItems;
import com.badkraft.foundations.world.level.block.ModBlocks;
import com.badkraft.foundations.world.level.block.entity.ModBlockEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;


@Mod("foundations")
public class Foundations {
    private static final Logger LOGGER = LogUtils.getLogger();

	public static final String MOD_ID = "foundations";
	public static Foundations INSTANCE;

	public static final CreativeModeTab FOUNDATIONS_TAB	 = new FoundationsTab(MOD_ID);
	
	public Foundations() {
		INSTANCE = this;
		LOGGER.info("*** *** Welcome to FOUNDATIONS!!! *** ***");
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		ModItems.register(bus);
		ModBlocks.register(bus);
		ModBlockEntities.register(bus);
		ModMenuContainers.register(bus);

		MinecraftForge.EVENT_BUS.register(this);
	}

	//	Foundations Category (Tab)
	private static class FoundationsTab extends CreativeModeTab {

		public FoundationsTab(String tabName) {
			super("%stab".formatted(tabName));
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public @NotNull ItemStack makeIcon() {
			return new ItemStack(ModItems.MEDALLION.get());
		}	
	}

}
