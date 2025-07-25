package org.jurassicraft.server.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jurassicraft.server.item.ItemHandler;

public class StorageSlot extends Slot {

    private final int stackLimit;
    private boolean stored;

    public StorageSlot(IInventory inventory, int slotIndex, int xPosition, int yPosition, boolean stored) {
        this(inventory, slotIndex, xPosition, yPosition, stored, 64);
    }

    public StorageSlot(IInventory inventory, int slotIndex, int xPosition, int yPosition, boolean stored, int stackLimit) {
        super(inventory, slotIndex, xPosition, yPosition);
        this.stored = stored;
        this.stackLimit = stackLimit;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (this.stored) {
            return stack.getItem() == ItemHandler.STORAGE_DISC && (stack.getTagCompound() != null && stack.getTagCompound().hasKey("DNAQuality"));
        } else {
            return stack.getItem() == ItemHandler.STORAGE_DISC && (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("DNAQuality"));
        }
    }
    
    @Override
    public int getSlotStackLimit() {
    	return stackLimit;
    }
}
