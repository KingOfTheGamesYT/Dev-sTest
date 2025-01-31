package com.devmaster.devs_test.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class LootChestTileEntity extends ChestTileEntity {
    private ResourceLocation lootTable;
    private boolean lootGenerated;
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY); // Default chest size

    public LootChestTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public LootChestTileEntity() {

    }

    public void setLootTable(ResourceLocation lootTable) {
        this.lootTable = lootTable;
        this.lootGenerated = false;
    }

    @Override
    public void openInventory(PlayerEntity player) {
        super.openInventory(player);
        if (!lootGenerated && lootTable != null && this.world instanceof ServerWorld) {
            generateLoot((ServerWorld) this.world, new Random());
            this.lootGenerated = true;
        }
    }

    private void generateLoot(ServerWorld world, Random random) {
        LootTable lootTableInstance = world.getServer().getLootTableManager().getLootTableFromLocation(lootTable);
        if (lootTableInstance != null) {
            LootContext.Builder builder = new LootContext.Builder(world)
                    .withParameter(LootParameters.ORIGIN, Vector3d.copyCentered(this.pos))
                    .withRandom(random);

            lootTableInstance.generate(builder.build(LootParameterSets.CHEST))
                    .forEach(this::addItemToInventory);
        }
    }

    private void addItemToInventory(ItemStack itemStack) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, itemStack);
                return;
            }
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        items = NonNullList.withSize(27, ItemStack.EMPTY);
        if (!this.checkLootAndRead(nbt)) {
            ItemStackHelper.loadAllItems(nbt, items);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        if (!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.saveAllItems(nbt, items);
        }
        return nbt;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Chest");
    }

    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return ChestContainer.createGeneric9X3(id, playerInventory, this);
    }
}
