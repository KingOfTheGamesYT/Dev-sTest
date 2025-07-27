package com.devmaster.devs_test.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockCrystal extends Block {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;


    public BlockCrystal() {
        super(AbstractBlock.Properties.create(Material.ROCK)
                .tickRandomly()
                .hardnessAndResistance(0.5F)
                .notSolid());
        this.setDefaultState(this.stateContainer.getBaseState()
                .with(STAGE, 0)
                .with(FACING, Direction.UP));
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        int currentStage = state.get(STAGE);
        Direction direction = state.get(FACING);
        BlockPos attachedTo = pos.offset(direction.getOpposite());

        Block attachedBlock = world.getBlockState(attachedTo).getBlock();

        if (attachedBlock.getRegistryName() != null && attachedBlock.getRegistryName().getPath().equals("budding_crystal")) {
            if (currentStage < 3) {
                world.setBlockState(pos, state.with(STAGE, currentStage + 1), 2);
            }
        }
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return state.get(STAGE) < 3;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(STAGE, FACING);
    }

    @Override
    public java.util.List<net.minecraft.item.ItemStack> getDrops(BlockState state, net.minecraft.loot.LootContext.Builder builder) {
        if (state.get(STAGE) == 3) {
            return super.getDrops(state, builder);
        } else {
            return java.util.Collections.emptyList(); // drop nothing
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction clickedFace = context.getFace(); // the face clicked
        return this.getDefaultState()
                .with(STAGE, 3) // full grown when placed manually
                .with(FACING, clickedFace);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        Direction facing = state.get(FACING);
        BlockPos attachedPos = pos.offset(facing.getOpposite());
        return world.getBlockState(attachedPos).isSolidSide(world, attachedPos, facing);
    }

}
