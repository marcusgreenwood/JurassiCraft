package org.jurassicraft.server.block.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.api.GrindableItem;
import org.jurassicraft.server.api.SequencableItem;
import org.jurassicraft.server.container.DNASequencerContainer;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.message.TileEntityFieldsMessage;

import com.google.common.primitives.Ints;

import io.netty.buffer.ByteBuf;

import java.util.Random;

public class DNASequencerBlockEntity extends MachineBaseBlockEntity {
    private static final int[] INPUTS = new int[] { 0, 1, 2, 3, 4, 5 };
    private static final int[] INPUTS_PROCESS_1 = new int[] { 0, 1 };
    private static final int[] INPUTS_PROCESS_2 = new int[] { 2, 3 };
    private static final int[] INPUTS_PROCESS_3 = new int[] { 4, 5 };

    private static final int[] OUTPUTS = new int[] { 6, 7, 8 };

    private NonNullList<ItemStack> slots = NonNullList.withSize(9, ItemStack.EMPTY);

    @Override
    protected int getProcess(int slot) {
        return Math.min(5, (int) Math.floor(slot / 2));
    }

    @Override
    protected boolean canProcess(int process) {
        int tissue = process * 2;

        ItemStack input = this.slots.get(tissue);
        ItemStack storage = this.slots.get(tissue + 1);

        SequencableItem sequencableItem = SequencableItem.getSequencableItem(input);

        if (sequencableItem != null && sequencableItem.isSequencable(input)) {
            if (!storage.isEmpty() && storage.getItem() == ItemHandler.STORAGE_DISC) {
                if (this.slots.get(process + 6).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void processItem(int process) {
        Random rand = new Random();

        int tissue = process * 2;

        ItemStack sequencableStack = this.slots.get(tissue);

        this.mergeStack(process + 6, SequencableItem.getSequencableItem(sequencableStack).getSequenceOutput(sequencableStack, rand));

        this.decreaseStackSize(tissue);
        this.decreaseStackSize(tissue + 1);
        BlockPos pos = this.pos;
        JurassiCraft.NETWORK_WRAPPER.sendToAllTracking(new TileEntityFieldsMessage(getSyncFields(NonNullList.create()), this), new TargetPoint(this.world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 5));
    }

    @Override
    protected int getMainOutput(int process) {
        return (process * 2) + 1;
    }

    @Override
    protected int getStackProcessTime(ItemStack stack) {
        return 2000;
    }

    @Override
    protected int getProcessCount() {
        return 3;
    }

    @Override
    protected int[] getInputs() {
        return INPUTS;
    }

    @Override
    protected int[] getInputs(int process) {
        if (process == 0) {
            return INPUTS_PROCESS_1;
        } else if (process == 1) {
            return INPUTS_PROCESS_2;
        }

        return INPUTS_PROCESS_3;
    }

    @Override
    protected int[] getOutputs() {
        return OUTPUTS;
    }

    @Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, EnumFacing side) {
		return Ints.asList(OUTPUTS).contains(slotID);
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
		
		if (Ints.asList(INPUTS).contains(slotID)) {
			if (slotID % 2 == 0) {
				if (itemstack != null && SequencableItem.getSequencableItem(itemstack) != null && this.getStackInSlot(slotID).getCount() == 0 && SequencableItem.getSequencableItem(itemstack).isSequencable(itemstack)) {
					return true;
				}
			} else {
				if (itemstack != null && itemstack.getItem() == ItemHandler.STORAGE_DISC && (itemstack.getTagCompound() == null || !itemstack.getTagCompound().hasKey("DNAQuality")) && this.getStackInSlot(slotID).getCount() == 0) {
					return true;
				}
			}

		}

		return false;
	}
    
    @Override
    protected NonNullList<ItemStack> getSlots() {
        return this.slots;
    }

    @Override
    protected void setSlots(NonNullList<ItemStack> slots) {
        this.slots = slots;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new DNASequencerContainer(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return JurassiCraft.MODID + ":dna_sequencer";
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.dna_sequencer";
    }
    
	@Override
	public void packetDataHandler(ByteBuf fields) {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			for (int slot : this.getSlotsForFace(Direction.UP)) {
				if (slot % 2 == 0) {
					this.setInventorySlotContents(slot, ByteBufUtils.readItemStack(fields));
				}
			}
		}
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		boolean send = false;
		if (!this.level().isClientSide && index % 2 == 0 && this.slots.get(index).getItem() != stack.getItem()) {
			send = true;
		}
		super.setInventorySlotContents(index, stack);

		if (send)
			JurassiCraft.NETWORK_WRAPPER.sendToAllTracking(new TileEntityFieldsMessage(getSyncFields(NonNullList.create()), this), new TargetPoint(this.world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 5));
	}
	
	@Override
	public NonNullList getSyncFields(NonNullList fields) {
		for (int slot : this.getSlotsForFace(Direction.UP)) {
			if (slot % 2 == 0) {
				fields.add(this.slots.get(slot));
			}
		}
		return fields;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}
