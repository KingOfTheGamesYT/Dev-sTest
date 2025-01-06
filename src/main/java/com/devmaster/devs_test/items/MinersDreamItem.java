package com.devmaster.devs_test.items;

import com.devmaster.devs_test.misc.Devs_Test;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.block.Blocks.*;
import static net.minecraft.util.Direction.*;

public class MinersDreamItem extends Item {

	public MinersDreamItem() {
		super(new Item.Properties().group(Devs_Test.ITEMS));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack stack = playerEntity.getHeldItem(hand);
		playerEntity.setActiveHand(hand);
		BlockPos pos = playerEntity.getPosition();
		Direction direction = playerEntity.getAdjustedHorizontalFacing();
		if(!playerEntity.abilities.isCreativeMode) {
			stack.shrink(1);
		}
		if(!world.isRemote) {
			for(int x = -5; x <= 5; x++) {
				for(int y = 0; y <= 5; y++) {
					for(int z = 0; z <= 50; z++) {
						BlockPos newPos = new BlockPos(pos);
						if(direction == SOUTH) {
							newPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
						}else if(direction == NORTH) {
							newPos = new BlockPos(pos.getX() - x, pos.getY() + y, pos.getZ() - z);
						}else if(direction == EAST) {
							newPos = new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() + x);
						}else if(direction == WEST) {
							newPos = new BlockPos(pos.getX() - z, pos.getY() + y, pos.getZ() - x);
						}

						BlockPos topPos = new BlockPos(newPos.getX(), newPos.getY() + 1, newPos.getZ());
						if(topPos.getY() <= 14 &&world.getBlockState(topPos) == AIR.getDefaultState() || world.getBlockState(topPos) == WATER.getDefaultState() || world.getBlockState(topPos) == LAVA.getDefaultState()) {
							world.setBlockState(topPos, COBBLESTONE.getDefaultState());
						}

						if (world.getBlockState(newPos).isIn(Devs_Test.MINERS_DREAM_MINEABLE)) {

							world.setBlockState(newPos, AIR.getDefaultState());
							if(x == 0 && y == 0 && z % 5 == 0) {
								world.setBlockState(newPos, TORCH.getDefaultState());
							}
						}
					}
				}
			}
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
		return new ActionResult<>(ActionResultType.FAIL, stack);
	}
}
