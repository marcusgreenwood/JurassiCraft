package org.jurassicraft.server.message;

import org.jurassicraft.server.entity.vehicle.FordExplorerEntity;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class FordExplorerUpdatePositionStateMessage extends AbstractMessage<FordExplorerUpdatePositionStateMessage>
{
    private int entityId;
    
    private long position;

    public FordExplorerUpdatePositionStateMessage()
    {}

    public FordExplorerUpdatePositionStateMessage(FordExplorerEntity entity, BlockPos railPos)
    {
        this.entityId = entity.getEntityId();
        this.position = railPos.toLong();
    }

    @Override
    public void onClientReceived(Minecraft minecraft, FordExplorerUpdatePositionStateMessage message, EntityPlayer player, MessageContext context)
    {
	Entity entity = player.world.getEntityByID(message.entityId);
        if (entity instanceof FordExplorerEntity)
        {
            FordExplorerEntity car = (FordExplorerEntity) entity;
            BlockPos prevRails = car.railTracks;
            car.railTracks = BlockPos.fromLong(message.position);
            car.prevPos = prevRails;
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, FordExplorerUpdatePositionStateMessage message, EntityPlayer player, MessageContext context)
    {}

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityId = buf.readInt();
        this.position = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityId);
        buf.writeLong(this.position);
    }
}