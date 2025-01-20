package com.devmaster.devs_test.items;

import com.devmaster.devs_test.misc.Devs_Test;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;


public class MinersDreamItem extends Item {

	public MinersDreamItem() {
		super(new Item.Properties());
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		BlockPos pos = context.getClickedPos();
		Direction direction = context.getHorizontalDirection();

		if (!level.isClientSide && player != null) {
			if (!player.getAbilities().instabuild) {  // Creative mode check
				stack.shrink(1); // Decrease item count by 1
			}

			for (int x = -5; x <= 5; x++) {
				for (int y = 0; y <= 5; y++) {
					for (int z = 0; z <= 50; z++) {
						BlockPos newPos = pos;
						if (direction == Direction.SOUTH) {
							newPos = pos.offset(x, y, z);
						} else if (direction == Direction.NORTH) {
							newPos = pos.offset(-x, y, -z);
						} else if (direction == Direction.EAST) {
							newPos = pos.offset(z, y, x);
						} else if (direction == Direction.WEST) {
							newPos = pos.offset(-z, y, -x);
						}

						BlockPos topPos = newPos.above(); // One block above newPos
						BlockState topBlockState = level.getBlockState(topPos);

						// Replace air/water/lava with cobblestone
						if (topPos.getY() <= 14 && (topBlockState.isAir() || topBlockState.is(Blocks.WATER) || topBlockState.is(Blocks.LAVA))) {
							level.setBlock(topPos, Blocks.COBBLESTONE.defaultBlockState(), 3);
						}

						// Check if the block is in the custom tag
						BlockState blockState = level.getBlockState(newPos);
						if (blockState.is(Devs_Test.MINERS_DREAM_MINEABLE)) {
							level.setBlock(newPos, Blocks.AIR.defaultBlockState(), 3);

							// Place a torch every 5 blocks along the z-axis
							if (x == 0 && y == 0 && z % 5 == 0) {
								level.setBlock(newPos, Blocks.TORCH.defaultBlockState(), 3);
							}
						}
					}
				}
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.FAIL;
	}
}