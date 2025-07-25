package org.jurassicraft.server.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jurassicraft.server.block.entity.CleaningStationBlockEntity;

public class WaterBucketSlot extends Slot {
    public WaterBucketSlot(IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
        super(inventoryIn, slotIndex, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return CleaningStationBlockEntity.isItemFuel(stack);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }
}
