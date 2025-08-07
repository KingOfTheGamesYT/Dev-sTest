package com.devmaster.devs_test.misc;

import com.devmaster.devs_test.util.RegistryHandler;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

@Mod("devs_test")
public class Devs_Test {
    public static final Logger LOGGER = LogManager.getLogger("Dev's Test");
    public static final String MOD_ID = "devs_test";
    public static final TagKey<Block> MINERS_DREAM_MINEABLE = TagKey.create(Registries.BLOCK, new ResourceLocation(Devs_Test.MOD_ID, "miners_dream_breakable"));

    public Devs_Test() {
       FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        RegistryHandler.init();
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BlacklistConfig.COMMON_CONFIG);

    }

    private void setup(FMLCommonSetupEvent event) {
        DungeonHooks.removeDungeonMob(EntityType.ZOMBIE);
        DungeonHooks.removeDungeonMob(EntityType.SKELETON);
        DungeonHooks.removeDungeonMob(EntityType.SPIDER);

        List<String> blacklist = (List<String>) BlacklistConfig.ENTITY_BLACKLIST.get();

        List<EntityType<?>> validMobs = ForgeRegistries.ENTITY_TYPES.getValues().stream()
                .filter(type -> {
                    if (type == null || type.getCategory() == null || BuiltInRegistries.ENTITY_TYPE.getKey(type) == null) return false;
                    if (type.getCategory().isFriendly()) return false;

                    String id = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
                    if (blacklist.contains(id)) {
                        System.out.println("[DungeonSpawner] Skipping blacklisted mob: " + id);
                        return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        System.out.println("[RandomDungeonSpawners] Adding " + validMobs.size() + " mobs to dungeon list");

        for (EntityType<?> type : validMobs) {
            DungeonHooks.addDungeonMob(type, 100); // Equal weight
        }
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }
}

