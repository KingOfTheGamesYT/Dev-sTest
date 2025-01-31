package com.devmaster.devs_test.blocks;

import com.devmaster.devs_test.entity.LootChestTileEntity;
import com.devmaster.devs_test.util.RegistryHandler;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class LootChestBlock extends ChestBlock {
    public LootChestBlock(Properties properties) {
        super(properties, () -> RegistryHandler.LOOT_CHEST_TILE_ENTITY.get());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof LootChestTileEntity) {
                player.openContainer((LootChestTileEntity) tileEntity); // Opens the chest GUI
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new LootChestTileEntity(RegistryHandler.LOOT_CHEST_TILE_ENTITY.get());
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) { // Compare the blocks directly
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof LootChestTileEntity) {
                InventoryHelper.dropInventoryItems(world, pos, (LootChestTileEntity) tileEntity);
                world.updateComparatorOutputLevel(pos, state.getBlock());
            }
            super.onReplaced(state, world, pos, newState, isMoving);
        }
    }
}