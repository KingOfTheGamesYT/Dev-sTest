package com.devmaster.devs_test.util;

import com.devmaster.devs_test.items.LootExtractor;
import com.devmaster.devs_test.items.MinersDreamItem;
import com.devmaster.devs_test.misc.Devs_Test;


import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class RegistryHandler {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Devs_Test.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Devs_Test.MOD_ID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    //Armor

    //Blocks

    //Block Items

    //Entities

    //Items
    public static final RegistryObject<Item> MINERS_DREAM = ITEMS.register("miners_dream", MinersDreamItem::new);
    public static final RegistryObject<Item> LOOT_EXTRACTOR = ITEMS.register("loot_extractor", LootExtractor::new);

    //Tools and Weapons

}