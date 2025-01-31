package com.devmaster.devs_test.entity;

import com.devmaster.devs_test.util.RegistryHandler;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.fml.network.NetworkHooks;

public class ThrowableSpawnEggEntity extends ThrowableEntity implements IRendersAsItem { // <-- Implementing IRendersAsItem

    private final ItemStack spawnEgg;

    public ThrowableSpawnEggEntity(EntityType<? extends ThrowableSpawnEggEntity> entityType, World world) {
        super(entityType, world);
        this.spawnEgg = ItemStack.EMPTY; // Default empty item
    }

    public ThrowableSpawnEggEntity(World world, PlayerEntity player, ItemStack spawnEgg) {
        super(RegistryHandler.THROWABLE_SPAWN_EGG.get(), player, world);
        this.spawnEgg = spawnEgg;
        this.setMotion(player.getLookVec().scale(1.5)); // Set velocity properly
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (spawnEgg.getItem() instanceof SpawnEggItem) {
                SpawnEggItem eggItem = (SpawnEggItem) spawnEgg.getItem();
                EntityType<?> entityType = eggItem.getType(spawnEgg.getTag());

                if (entityType != null) {
                    Entity entity = entityType.create(world);
                    if (entity instanceof MobEntity) {
                        entity.setPosition(getPosX(), getPosY(), getPosZ());
                        world.addEntity(entity);
                    }
                }
            }
            this.remove();
        }
    }

    //Todo  // ✅ Fix for slimes: Randomize their size like vanilla behavior, Fix mobs with varients not having their varients, Fix the Egg texture

    @Override
    protected void registerData() {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public ItemStack getItem() {
        return spawnEgg.isEmpty() ? new ItemStack(Items.EGG) : spawnEgg; // Fallback to a regular egg if empty
    }
}