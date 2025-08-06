package com.devmaster.devs_test.misc;

import com.devmaster.devs_test.client.render.renderers.FrogRenderer;
import com.devmaster.devs_test.config.BlacklistConfig;
import com.devmaster.devs_test.entity.FrogEntity;
import com.devmaster.devs_test.util.RegistryHandler;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;


@Mod("devs_test")
public class Devs_Test {
    public static final Logger LOGGER = LogManager.getLogger("Dev's Test");
    public static final String MOD_ID = "devs_test";
    public static final ItemGroup TAB = new LootPlacerTab();

    public static final ITag.INamedTag<Block> MINERS_DREAM_MINEABLE = BlockTags.makeWrapperTag(Devs_Test.MOD_ID+":miners_dream_breakable");

    public Devs_Test() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        RegistryHandler.init();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new SpawnEggThrowHandler()); // ✅ Registers SpawnEggThrowHandler properly
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BlacklistConfig.COMMON_CONFIG, "devs_test-common.toml");

    }

    private void setup(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(RegistryHandler.FROG.get(), FrogEntity.getAttributes().create());
        });

        ItemModelsProperties.registerProperty(
                RegistryHandler.MOB_CAGE.get(),
                new ResourceLocation("mob_type"),
                (stack, world, entity) -> {
                    if (!stack.hasTag()) return 0.0F;

                    CompoundNBT tag = stack.getTag();
                    String mob = tag.getString("MobID");
                    String mount = tag.contains("MountID") ? tag.getString("MountID") : "";

                    // Charged Creeper
                    if (mob.equals("minecraft:creeper") && tag.contains("MobData")) {
                        CompoundNBT data = tag.getCompound("MobData");
                        if (data.getBoolean("powered")) return 20.0F;
                    }

                    // Special combos
                    if (mob.equals("minecraft:zombie") && mount.equals("minecraft:chicken")) return 10.0F;
                    if (mob.equals("minecraft:skeleton") && mount.equals("minecraft:spider")) return 11.0F;

                    // Known mob types
                    switch (mob) {
                        case "minecraft:zombie":
                            return 1.0F;
                        case "minecraft:creeper":
                            return 2.0F;
                        case "minecraft:skeleton":
                            return 3.0F;
                        case "iceandfire:fire_dragon":
                            return 4.0F;
                        case "iceandfire:cyclops":
                            return 5.0F;
                        default:
                            // Mob captured but unsupported → show "unknown"
                            return 99.0F;
                    }
                }
        );

        DungeonHooks.removeDungeonMob(EntityType.ZOMBIE);
        DungeonHooks.removeDungeonMob(EntityType.SKELETON);
        DungeonHooks.removeDungeonMob(EntityType.SPIDER);

        List<String> blacklist = (List<String>) BlacklistConfig.ENTITY_BLACKLIST.get();

        List<EntityType<?>> validMobs = ForgeRegistries.ENTITIES.getValues().stream()
                .filter(type -> {
                    if (type == null || type.getClassification() == null || type.getRegistryName() == null) return false;
                    if (type.getClassification().getPeacefulCreature()) return false;

                    String id = type.getRegistryName().toString();
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
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.THROWABLE_SPAWN_EGG.get(),
                manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.THROWABLE_CAGE.get(),
                manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderTypeLookup.setRenderLayer(RegistryHandler.APPLE_LEAVES.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.BUDDING_PIK_TOURMALINE.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.BUDDING_CATS_EYE.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.BUDDING_CRYSTAL.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.CRYSTAL.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.PINK_TOURMALINE_CLUSTER.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.CATS_EYE_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.FIREFLYPLANT.get(), RenderType.getCutout());

        RenderingRegistry.registerEntityRenderingHandler(RegistryHandler.FROG.get(), FrogRenderer::new);

    }


    public static final ItemGroup ITEMS = new ItemGroup("main") {

        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.MINERS_DREAM.get());
        }
    };

    public static final ItemGroup TEST = new ItemGroup("test") {

        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.ALUMINUM_INGOT.get());
        }
    };
}