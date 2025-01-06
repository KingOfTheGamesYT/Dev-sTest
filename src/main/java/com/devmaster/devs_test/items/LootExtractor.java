package com.devmaster.devs_test.items;

import com.devmaster.devs_test.misc.Devs_Test;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;

import java.util.List;
import java.util.Optional;

public class LootExtractor extends Item {

    public LootExtractor() {
        super(new Item.Properties().group(Devs_Test.ITEMS));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);

        // Perform entity ray tracing
        EntityRayTraceResult entityRayTraceResult = rayTraceEntities(world, player, 5.0D); // Adjust range as needed
        if (entityRayTraceResult != null && entityRayTraceResult.getEntity() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) entityRayTraceResult.getEntity();

            if (!world.isRemote) {
                // Generate loot from the target's loot table
                dropLootFromEntity((ServerWorld) world, target, player);
            }

            player.swing(hand, true);
            return ActionResult.resultSuccess(itemStack);
        }

        return ActionResult.resultFail(itemStack);
    }

    private EntityRayTraceResult rayTraceEntities(World world, PlayerEntity player, double range) {
        Vector3d start = player.getEyePosition(1.0F);
        Vector3d look = player.getLookVec();
        Vector3d end = start.add(look.scale(range));

        // Create a bounding box along the ray trace path
        AxisAlignedBB boundingBox = player.getBoundingBox().expand(look.scale(range)).grow(1.0D);

        // Check for entities in the bounding box
        List<Entity> entities = world.getEntitiesInAABBexcluding(player, boundingBox, entity -> entity instanceof LivingEntity && entity.isAlive());
        Entity closestEntity = null;
        double closestDistance = range * range;

        for (Entity entity : entities) {
            AxisAlignedBB entityBoundingBox = entity.getBoundingBox().grow(0.3D);
            Optional<Vector3d> optionalHitVec = entityBoundingBox.rayTrace(start, end);

            if (optionalHitVec.isPresent()) {
                double distance = start.squareDistanceTo(optionalHitVec.get());
                if (distance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        return closestEntity != null ? new EntityRayTraceResult(closestEntity) : null;
    }

    private void dropLootFromEntity(ServerWorld world, LivingEntity target, PlayerEntity player) {
        // Get the LootTableResourceLocation for the target entity
        ResourceLocation lootTableLocation = target.getLootTableResourceLocation();

        if (lootTableLocation != null) {
            // Fetch the loot table using the LootTableManager
            LootContext.Builder lootBuilder = new LootContext.Builder(world)
                    .withParameter(LootParameters.THIS_ENTITY, target)
                    .withParameter(LootParameters.ORIGIN, target.getPositionVec())
                    .withParameter(LootParameters.DAMAGE_SOURCE, DamageSource.MAGIC)
                    .withParameter(LootParameters.KILLER_ENTITY, player);

            LootContext lootContext = lootBuilder.build(LootParameterSets.ENTITY);
            List<ItemStack> loot = world.getServer().getLootTableManager().getLootTableFromLocation(lootTableLocation).generate(lootContext);

            // Drop each item in the world at the target's position
            for (ItemStack stack : loot) {
                world.addEntity(new ItemEntity(world, target.getPosX(), target.getPosY(), target.getPosZ(), stack));
            }
        }
    }
}