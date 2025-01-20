package com.devmaster.devs_test.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;


public class LootExtractor extends Item {

    public LootExtractor() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();

        if (!level.isClientSide && target != null) {
            // Generate loot from the target's loot table
            dropLootFromEntity((ServerLevel) level, target, player);

            // Swing the hand to show the action
            player.swing(hand, true);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    private EntityHitResult rayTraceEntities(Level level, Player player, double range) {
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();
        Vec3 end = start.add(look.scale(range));

        // Create a bounding box along the ray trace path
        AABB boundingBox = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0D);

        // Check for entities in the bounding box
        List<Entity> entities = level.getEntities(player, boundingBox, entity -> entity instanceof LivingEntity && entity.isAlive());
        Entity closestEntity = null;
        double closestDistance = range * range;

        for (Entity entity : entities) {
            AABB entityBoundingBox = entity.getBoundingBox().inflate(0.3D);
            Optional<Vec3> optionalHitVec = entityBoundingBox.clip(start, end);

            if (optionalHitVec.isPresent()) {
                double distance = start.distanceToSqr(optionalHitVec.get());
                if (distance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        return closestEntity != null ? new EntityHitResult(closestEntity) : null;
    }

    private void dropLootFromEntity(ServerLevel level, LivingEntity target, Player player) {
        // Get the loot table for the target entity
        ResourceLocation lootTableLocation = target.getType().getDefaultLootTable();

        if (lootTableLocation != null) {
            // Build the loot context with parameters
            LootParams.Builder lootBuilder = new LootParams.Builder(level)
                    .withParameter(LootContextParams.THIS_ENTITY, target)
                    .withParameter(LootContextParams.ORIGIN, target.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().playerAttack(player))
                    .withOptionalParameter(LootContextParams.KILLER_ENTITY, player);

            // Generate loot
            List<ItemStack> loot = level.getServer().getLootData().getLootTable(lootTableLocation).getRandomItems(lootBuilder.create(LootContextParamSets.ENTITY));

            // Drop each item in the world at the target's position
            for (ItemStack stack : loot) {
                level.addFreshEntity(new ItemEntity(level, target.getX(), target.getY(), target.getZ(), stack));
            }

            }
    }
}