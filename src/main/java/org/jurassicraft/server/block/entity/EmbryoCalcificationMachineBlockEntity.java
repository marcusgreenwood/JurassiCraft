package org.jurassicraft.server.block.entity;

import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.api.GrindableItem;
import org.jurassicraft.server.api.SynthesizableItem;
import org.jurassicraft.server.container.EmbryoCalcificationMachineContainer;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.dinosaur.Dinosaur.BirthType;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.item.SyringeItem;

import com.google.common.primitives.Ints;

import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.util.NonNullList;

public class EmbryoCalcificationMachineBlockEntity extends MachineBaseBlockEntity {
    private static final int[] INPUTS = new int[] { 0, 1 };
    private static final int[] OUTPUTS = new int[] { 2 };

    private NonNullList<ItemStack> slots = NonNullList.withSize(3, ItemStack.EMPTY);

    @Override
    protected int getProcess(int slot) {
        return 0;
    }

    @Override
    protected boolean canProcess(int process) {
        ItemStack input = this.slots.get(0);
        ItemStack egg = this.slots.get(1);

        if (!input.isEmpty() && input.getItem() instanceof SyringeItem && !egg.isEmpty() && egg.getItem() == Items.EGG) {
            Dinosaur dino = EntityHandler.getDinosaurById(input.getItemDamage());

            if (dino.getMetadata().getBirthType() == Dinosaur.BirthType.EGG_LAYING) {
                ItemStack output = new ItemStack(ItemHandler.EGG, 1, input.getItemDamage());
                output.setTagCompound(input.getTagCompound());

                return this.hasOutputSlot(output);
            }
        }

        return false;
    }

    @Override
    protected void processItem(int process) {
        if (this.canProcess(process)) {
            ItemStack output = new ItemStack(ItemHandler.EGG, 1, this.slots.get(0).getItemDamage());
            output.setTagCompound(this.slots.get(0).getTagCompound());

            this.mergeStack(2, output);
            this.decreaseStackSize(0);
            this.decreaseStackSize(1);
        }
    }

    @Override
    protected int getMainOutput(int process) {
        return 1;
    }

    @Override
    protected int getStackProcessTime(ItemStack stack) {
        return 200;
    }

    @Override
    protected int getProcessCount() {
        return 1;
    }

    @Override
    protected int[] getInputs() {
        return INPUTS;
    }

    @Override
    protected int[] getInputs(int process) {
        return this.getInputs();
    }

    @Override
    protected int[] getOutputs() {
        return OUTPUTS;
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
        return new EmbryoCalcificationMachineContainer(playerInventory, this);
    }

    @Override
    public String getGuiID() {
    	return JurassiCraft.MODID + ":em_calcification_m";
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.embryo_calcification_machine";
    }
    
    @Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, EnumFacing side) {
		return Ints.asList(OUTPUTS).contains(slotID);
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
		if (Ints.asList(INPUTS).contains(slotID)) {
			if ((slotID == 0 && itemstack != null && itemstack.getItem() instanceof SyringeItem && SyringeItem.getDinosaur(itemstack).getMetadata().getBirthType() == BirthType.EGG_LAYING)
					|| slotID == 1 && itemstack != null && itemstack.getItem() == Items.EGG) {

				return true;
			}
		}

		return false;
	}

    @Override
    protected void onSlotUpdate() {
        super.onSlotUpdate();
        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

	@Override
	public boolean isEmpty() {
		return false;
	}
}
