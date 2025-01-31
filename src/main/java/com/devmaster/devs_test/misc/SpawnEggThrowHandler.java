package com.devmaster.devs_test.misc;

import com.devmaster.devs_test.entity.ThrowableSpawnEggEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpawnEggThrowHandler {

    @SubscribeEvent
    public static void onSpawnEggUse(PlayerInteractEvent.RightClickItem event) {
        PlayerEntity player = event.getPlayer();
        World world = event.getWorld();
        ItemStack itemStack = event.getItemStack();

        if (!world.isRemote && itemStack.getItem() instanceof SpawnEggItem) {
            if (!player.isCreative()) {
                itemStack.shrink(1);
            }

            ThrowableSpawnEggEntity eggEntity = new ThrowableSpawnEggEntity(world, player, itemStack);
            eggEntity.setMotion(player.getLookVec().scale(1.5));

            world.addEntity(eggEntity);

            event.setCanceled(true);
            event.setCancellationResult(ActionResultType.SUCCESS);
        }
    }
}
