package org.jurassicraft.server.message;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.block.entity.TemperatureControl;

public class ChangeTemperatureMessage extends AbstractMessage<ChangeTemperatureMessage> {
    private int index;
    private int temperature;
    private BlockPos pos;
    private int dimension;

    public ChangeTemperatureMessage() {
    }

    public ChangeTemperatureMessage(BlockPos pos, int index, int temperature, int dimension) {
        this.index = index;
        this.temperature = temperature;
        this.pos = pos;
        this.dimension = dimension;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, ChangeTemperatureMessage message, EntityPlayer player, MessageContext messageContext) {
        TileEntity tile = player.world.getTileEntity(message.pos);
        if (tile instanceof TemperatureControl) {
            TemperatureControl control = (TemperatureControl) tile;
            control.setTemperature(message.index, message.temperature);
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, ChangeTemperatureMessage message, EntityPlayer player, MessageContext messageContext) {
        TileEntity tile = player.world.getTileEntity(message.pos);
        if (tile instanceof TemperatureControl) {
            TemperatureControl control = (TemperatureControl) tile;
            if (control.isUsableByPlayer(player) && message.index >= 0 && message.index < control.getTemperatureCount()) {
                control.setTemperature(message.index, message.temperature);
                JurassiCraft.NETWORK_WRAPPER.sendToAllTracking(new ChangeTemperatureMessage(message.pos, message.index, message.temperature, message.dimension), new TargetPoint(message.dimension, message.pos.getX(),  message.pos.getY(),  message.pos.getZ(), 5));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(this.index);
        buffer.writeByte(this.temperature);
        buffer.writeLong(this.pos.toLong());
        buffer.writeInt(this.dimension);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.index = buffer.readUnsignedByte();
        this.temperature = buffer.readUnsignedByte();
        this.pos = BlockPos.fromLong(buffer.readLong());
        this.dimension = buffer.readInt();
    }
}
