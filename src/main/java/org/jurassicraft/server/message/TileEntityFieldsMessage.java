package org.jurassicraft.server.message;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.jurassicraft.server.block.entity.ISyncable;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.entity.item.PaddockSignEntity;
import org.jurassicraft.server.item.ItemHandler;

public class TileEntityFieldsMessage extends AbstractMessage<TileEntityFieldsMessage> {
	
	public NonNullList fields;
	private BlockPos position;
	
	public ByteBuf fieldBuffer = null;

    public TileEntityFieldsMessage() {
    }

    public TileEntityFieldsMessage(NonNullList fields, TileEntity blockEntity) {
    	
    	this.fields = fields;
    	this.position = blockEntity.getPos();
    }

	@Override
	public void onClientReceived(Minecraft minecraft, TileEntityFieldsMessage message, EntityPlayer player, MessageContext messageContext) {

		TileEntity tileEntity = player.world.getTileEntity(message.position);
		if (tileEntity != null) {
			ISyncable syncable = (ISyncable) tileEntity;
			if (message.fieldBuffer != null) {
				try {
					syncable.packetDataHandler(message.fieldBuffer);
				} catch (Exception e) {
					e.printStackTrace();
				}
				message.fieldBuffer.release();
			}
		}

	}

    @Override
    public void onServerReceived(MinecraftServer server, TileEntityFieldsMessage message, EntityPlayer player, MessageContext messageContext) {}

    @Override
    public void toBytes(ByteBuf buffer) {
    	buffer.writeLong(this.position.toLong());
    	Object[] objL = this.fields.toArray();
    	for(Object obj : objL)
		{
    		
    		if(obj instanceof Boolean)
			{
				buffer.writeBoolean((Boolean)obj);
			}
    		else if(obj instanceof Byte)
			{
				buffer.writeByte((Byte)obj);
			}
			else if(obj instanceof Integer)
			{
				buffer.writeInt((Integer)obj);
			}
			else if(obj instanceof Short)
			{
				buffer.writeShort((Short)obj);
			}
			else if(obj instanceof Long)
			{
				buffer.writeLong((Long)obj);
			}
			else if(obj instanceof Float)
			{
				buffer.writeFloat((Float)obj);
			}
			else if(obj instanceof Double)
			{
				buffer.writeDouble((Double)obj);
			}
			else if(obj instanceof String)
			{
				ByteBufUtils.writeUTF8String(buffer, (String)obj);
			}
			else if(obj instanceof NBTTagCompound)
			{
				ByteBufUtils.writeTag(buffer, (NBTTagCompound)obj);
			}
			else if(obj instanceof ItemStack)
			{
				ByteBufUtils.writeItemStack(buffer, (ItemStack)obj);
			}
		}
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
    	this.position = BlockPos.fromLong(buffer.readLong());
    	fieldBuffer = buffer.copy();
    }
}
