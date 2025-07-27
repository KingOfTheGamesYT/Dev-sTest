package com.devmaster.devs_test.blocks;

import com.devmaster.devs_test.util.RegistryHandler;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockBuddingCrystal extends Block {

    public BlockBuddingCrystal() {
        super(AbstractBlock.Properties.create(Material.ROCK)
                .tickRandomly()
                .hardnessAndResistance(2.0F)
                .notSolid());
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        Direction direction = Direction.getRandomDirection(rand);
        BlockPos targetPos = pos.offset(direction); // ✅ grow in that direction

        if (world.isAirBlock(targetPos)) {
            world.setBlockState(targetPos, RegistryHandler.CRYSTAL.get().getDefaultState()
                    .with(BlockCrystal.STAGE, 0)
                    .with(BlockCrystal.FACING, direction)); // ✅ match the growth direction
        }
    }


    @Override
    public boolean ticksRandomly(BlockState state) {
        return true;
    }
}
