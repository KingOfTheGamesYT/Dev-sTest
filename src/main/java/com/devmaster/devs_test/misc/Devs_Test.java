package com.devmaster.devs_test.misc;

import com.devmaster.devs_test.util.RegistryHandler;

import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("devs_test")
public class Devs_Test {
    public static final Logger LOGGER = LogManager.getLogger("Dev's Test");
    public static final String MOD_ID = "devs_test";
    public static final ITag.INamedTag<Block> MINERS_DREAM_MINEABLE = BlockTags.makeWrapperTag(Devs_Test.MOD_ID+":miners_dream_breakable");

    public Devs_Test() {
       FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        RegistryHandler.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }
    private void doClientStuff(final FMLClientSetupEvent event) {
    }

    public static final ItemGroup ITEMS = new ItemGroup("main") {

        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.MINERS_DREAM.get());
        }
    };
}

