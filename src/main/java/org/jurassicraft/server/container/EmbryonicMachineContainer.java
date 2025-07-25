package org.jurassicraft.server.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import org.jurassicraft.server.block.entity.EmbryonicMachineBlockEntity;
import org.jurassicraft.server.container.slot.CustomSlot;
import org.jurassicraft.server.container.slot.PetriDishSlot;
import org.jurassicraft.server.container.slot.TestTubeSlot;
import org.jurassicraft.server.item.ItemHandler;

public class EmbryonicMachineContainer extends MachineContainer {
    private EmbryonicMachineBlockEntity embryonicMachine;

    public EmbryonicMachineContainer(InventoryPlayer playerInventory, TileEntity tileEntity) {
        super((IInventory) tileEntity);

        this.embryonicMachine = (EmbryonicMachineBlockEntity) tileEntity;
        this.addSlotToContainer(new TestTubeSlot(this.embryonicMachine, 0, 24, 49));
        this.addSlotToContainer(new PetriDishSlot(this.embryonicMachine, 1, 50, 49));
        this.addSlotToContainer(new CustomSlot(this.embryonicMachine, 2, 50, 13, stack -> stack.getItem() == ItemHandler.EMPTY_SYRINGE));

        int i;

        for (i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlotToContainer(new CustomSlot(this.embryonicMachine, i + (j * 2) + 3, i * 18 + 119, j * 18 + 26, stack -> false));
            }
        }

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        if (!player.level().isClientSide) {
            this.embryonicMachine.closeInventory(player);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.embryonicMachine.isUsableByPlayer(player);
    }
}
