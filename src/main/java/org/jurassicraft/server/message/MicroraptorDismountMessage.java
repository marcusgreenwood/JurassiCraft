package org.jurassicraft.server.message;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.entity.dinosaur.MicroraptorEntity;

import java.util.Set;

public class MicroraptorDismountMessage extends AbstractMessage<MicroraptorDismountMessage> {
    private int entityId;

    public MicroraptorDismountMessage(int entityId) {
        this.entityId = entityId;
    }

    public MicroraptorDismountMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
    }

    @Override
    public void onClientReceived(Minecraft client, MicroraptorDismountMessage message, EntityPlayer player, MessageContext messageContext) {
        Entity entity = client.world.getEntityByID(message.entityId);
        if (entity instanceof MicroraptorEntity) {
            entity.dismountRidingEntity();
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, MicroraptorDismountMessage message, EntityPlayer player, MessageContext messageContext) {
        Entity entity = player.world.getEntityByID(message.entityId);
        if (entity instanceof MicroraptorEntity) {
            MicroraptorEntity microraptor = (MicroraptorEntity) entity;
            if (microraptor.isOwner(player)) {
                microraptor.dismountRidingEntity();
                if (!player.level().isClientSide) {
                    WorldServer worldServer = (WorldServer) player.world;
                    Set<? extends EntityPlayer> trackers = worldServer.getEntityTracker().getTrackingPlayers(microraptor);
                    for (EntityPlayer tracker : trackers) {
                        JurassiCraft.NETWORK_WRAPPER.sendTo(message, (EntityPlayerMP) tracker);
                    }
                }
            }
        }
    }
}