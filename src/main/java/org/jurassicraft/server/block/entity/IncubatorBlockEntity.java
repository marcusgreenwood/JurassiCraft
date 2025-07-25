package org.jurassicraft.server.block.entity;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.api.IncubatorEnvironmentItem;
import org.jurassicraft.server.container.IncubatorContainer;
import org.jurassicraft.server.item.DinosaurEggItem;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.message.TileEntityFieldsMessage;
import org.jurassicraft.server.plugin.waila.IWailaProvider;
import com.google.common.primitives.Ints;
import io.netty.buffer.ByteBuf;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class IncubatorBlockEntity extends MachineBaseBlockEntity implements TemperatureControl, IWailaProvider {
    private static final int[] INPUTS = new int[] { 0, 1, 2, 3, 4 };
    private static final int[] ENVIRONMENT = new int[] { 5 };

    private int[] temperature = new int[5];

    private NonNullList<ItemStack> slots = NonNullList.withSize(6, ItemStack.EMPTY);

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        for (int i = 0; i < this.getProcessCount(); i++) {
            this.temperature[i] = compound.getShort("Temperature" + i);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        for (int i = 0; i < this.getProcessCount(); i++) {
            compound.setShort("Temperature" + i, (short) this.temperature[i]);
        }

        return compound;
    }

    @Override
    protected int getProcess(int slot) {
        if (slot == 5) {
            return -1;
        } else {
            return slot;
        }
    }

    @Override
    protected boolean canProcess(int process) {
        ItemStack environment = this.slots.get(5);
        boolean hasEnvironment = false;

        if (!environment.isEmpty()) {
            Item item = environment.getItem();

            if (item instanceof IncubatorEnvironmentItem || Block.getBlockFromItem(item) instanceof IncubatorEnvironmentItem) {
                hasEnvironment = true;
            }
        }

        return hasEnvironment && !this.slots.get(process).isEmpty() && this.slots.get(process).getCount() > 0 && this.slots.get(process).getItem() instanceof DinosaurEggItem;
    }

    @Override
    protected void processItem(int process) {
        if (this.canProcess(process) && !this.level().isClientSide) {
            ItemStack egg = this.slots.get(process);

            ItemStack incubatedEgg = new ItemStack(ItemHandler.HATCHED_EGG, 1, egg.getItemDamage());
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("Gender", this.temperature[process] > 50);

            if (egg.getTagCompound() != null) {
                compound.setString("Genetics", egg.getTagCompound().getString("Genetics"));
                compound.setInteger("DNAQuality", egg.getTagCompound().getInteger("DNAQuality"));
            }

            incubatedEgg.setTagCompound(compound);

            this.decreaseStackSize(5);
            this.setInventorySlotContents(process, incubatedEgg);
        }
    }

    @Override
    protected int getMainOutput(int process) {
        return 0;
    }

    @Override
    protected int getStackProcessTime(ItemStack stack) {
        return 8000;
    }

    @Override
    protected int getProcessCount() {
        return 5;
    }

    @Override
    protected int[] getInputs() {
        return INPUTS;
    }

    @Override
    protected int[] getInputs(int process) {
        return new int[] { process };
    }

    @Override
    protected int[] getOutputs() {
        return ENVIRONMENT;
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
        return new IncubatorContainer(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return JurassiCraft.MODID + ":incubator";
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.incubator";
    }

    @Override
    public int getField(int id) {
        if (id < 5) {
            return this.processTime[id];
        } else if (id < 10) {
            return this.totalProcessTime[id - 5];
        } else if (id < 15) {
            return this.temperature[id - 10];
        }

        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id < 5) {
            this.processTime[id] = value;
        } else if (id < 10) {
            this.totalProcessTime[id - 5] = value;
        } else if (id < 15) {
            this.temperature[id - 10] = value;
        }
    }
    
    @Override
	public int[] getSlotsForFace(EnumFacing side) {
		
		return ArrayUtils.addAll(INPUTS, ENVIRONMENT);
	}
    
    @Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack) {
		if (Ints.asList(INPUTS).contains(slotID)) {
			if (itemstack != null && itemstack.getItem() == ItemHandler.EGG) {
				return true;
			}
		}else if(Ints.asList(ENVIRONMENT).contains(slotID)) {
			if (itemstack != null && itemstack.getItem() instanceof IncubatorEnvironmentItem || Block.getBlockFromItem(itemstack.getItem()) instanceof IncubatorEnvironmentItem) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	public void update() {
		boolean send = false;
		if (!world.isRemote && FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter() % 100 == 0) {

			for (int i = 0; i < 5; i++) {
				if (this.isProcessing(i)) {
					send = true;
					break;
				}
			}
		}
		super.update();

		if (send && !world.isRemote) {
			final BlockPos pos = this.getPos();
			JurassiCraft.NETWORK_WRAPPER.sendToAllTracking(new TileEntityFieldsMessage(this.getSyncFields(NonNullList.create()), this), new TargetPoint(this.world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 5));
		}

	}
	
	@Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return (T) handlerPull;
        return super.getCapability(capability, facing);
    }

    @Override
    protected boolean shouldResetProgress() {
        return false;
    }

    @Override
    public void setTemperature(int index, int value) {
        this.temperature[index] = value;
    }

    @Override
    public int getTemperature(int index) {
        return this.temperature[index];
    }

    @Override
    public int getTemperatureCount() {
        return this.temperature.length;
    }

	@Override
	public boolean isEmpty() {
		return false;
	}
	
	IItemHandler handlerPull = new IncubatorWrapper(this, this, null) {

		@Override
		@Nonnull
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (Ints.asList(INPUTS).contains(slot)) {
				ItemStack stackInSlot = inv.getStackInSlot(slot);
				if (stackInSlot != null && stackInSlot.getItem() == ItemHandler.HATCHED_EGG) {
					ItemStack extract = super.extractItem(slot, amount, simulate);
					final BlockPos pos = this.getTile().getPos();
					JurassiCraft.NETWORK_WRAPPER.sendToAllTracking(new TileEntityFieldsMessage(this.getTile().getSyncFields(NonNullList.create()), this.getTile()), new TargetPoint(this.getTile().world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 5));
					return extract;
				}

			}
			return ItemStack.EMPTY;

		}
	};
	
	@Override
	public void packetDataHandler(ByteBuf fields) {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			for (int slot = 0; slot < 5; slot++) {
				this.setInventorySlotContents(slot, ByteBufUtils.readItemStack(fields));
			}
			for (int field = 0; field < 10; field++) {
				this.setField(field, fields.readInt());
			}
		}
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		boolean send = false;
		if (!this.level().isClientSide && index >= 0 && index <= 4 && this.slots.get(index).getItem() != stack.getItem()) {
			send = true;
		}
		super.setInventorySlotContents(index, stack);

		if (send) {
			final BlockPos pos = this.getPos();
			JurassiCraft.NETWORK_WRAPPER.sendToAllTracking(new TileEntityFieldsMessage(this.getSyncFields(NonNullList.create()), this), new TargetPoint(this.world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 5));
		}
	}
	
	@Override
	public NonNullList getSyncFields(NonNullList fields) {
		for (int slot = 0; slot < 5; slot++) {
			fields.add(this.slots.get(slot));
		}
		
		for (int field = 0; field < 10; field++) {
			fields.add(this.getField(field));
		}
		return fields;
	}
	
	private class IncubatorWrapper extends SidedInvWrapper {
		
		private final IncubatorBlockEntity tile;

		public IncubatorWrapper(IncubatorBlockEntity tile, ISidedInventory inv, EnumFacing side) {
			super(inv, side);
			this.tile = tile;
		}
		
		public IncubatorBlockEntity getTile() {
			return this.tile;
		}
		
	}

	@Override
	public List<String> getWailaData(List<String> list) {
		List<String> content = new ArrayList<String>();
		for(int input = 0; input < INPUTS.length; input++) {
			ItemStack stack = this.getStackInSlot(input);
			if(stack != null && !stack.isEmpty()) {
				content.add(input + ";" + stack.getItem().getRegistryName().toString() + ";" + stack.getMetadata() + ";" + getProgress(input) + ";" + (this.temperature[input] > 50) + ";" + (stack.hasTagCompound() ? stack.getTagCompound().toString() : "{}"));
			}
		}
		if(content.size() > 0)
			list.add(SpecialChars.getRenderString("jurassicraft.incubator", content.toArray(new String[0])));
		return list;
	}

    
	private int getProgress(int slot) {
        int j = this.getField(slot);
        int k = this.getField(slot + 5);
        return k != 0 && j != 0 ? j * 14 / k : 0;
    }
}
