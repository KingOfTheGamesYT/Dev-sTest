package com.devmaster.devs_test.util;

import com.devmaster.devs_test.blocks.LootChestBlock;
import com.devmaster.devs_test.entity.LootChestTileEntity;
import com.devmaster.devs_test.entity.ThrowableSpawnEggEntity;
import com.devmaster.devs_test.items.LootExtractor;
import com.devmaster.devs_test.items.MinersDreamItem;
import com.devmaster.devs_test.misc.Devs_Test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class RegistryHandler {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Devs_Test.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Devs_Test.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Devs_Test.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Devs_Test.MOD_ID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());

    }

    //Armor

    //Blocks
    public static final RegistryObject<Block> LOOT_CHEST_BLOCK = BLOCKS.register("loot_chest",
            () -> new LootChestBlock(Block.Properties.create(Material.WOOD, MaterialColor.WOOD)
                    .hardnessAndResistance(2.5F)
                    .notSolid()));

    //Block Items
    public static final RegistryObject<Item> LOOT_CHEST_ITEM = ITEMS.register("loot_chest",
            () -> new BlockItem(LOOT_CHEST_BLOCK.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));

    // Tile Entities
    public static final RegistryObject<TileEntityType<LootChestTileEntity>> LOOT_CHEST_TILE_ENTITY = TILE_ENTITIES.register("loot_chest_tile",
            () -> TileEntityType.Builder.create(() -> new LootChestTileEntity(), LOOT_CHEST_BLOCK.get()).build(null));

    //Entities
    public static final RegistryObject<EntityType<ThrowableSpawnEggEntity>> THROWABLE_SPAWN_EGG = ENTITIES.register("throwable_spawn_egg",
            () -> EntityType.Builder.<ThrowableSpawnEggEntity>create(ThrowableSpawnEggEntity::new, EntityClassification.MISC)
                    .size(0.25F, 0.25F)
                    .build("throwable_spawn_egg"));;


    //Items
    public static final RegistryObject<Item> MINERS_DREAM = ITEMS.register("miners_dream", MinersDreamItem::new);
    public static final RegistryObject<Item> LOOT_EXTRACTOR = ITEMS.register("loot_extractor", LootExtractor::new);

    //Tools and Weapons

}