package org.jurassicraft.server.message;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.entity.DinosaurEntity;

public class OpenFieldGuideGuiMessage extends AbstractMessage<OpenFieldGuideGuiMessage> {
    private DinosaurEntity entity;
    private int entityId;
    private boolean flocking;
    private boolean fleeing;
    private DinosaurEntity.FieldGuideInfo fieldGuideInfo;

    public OpenFieldGuideGuiMessage() {
    }

    public OpenFieldGuideGuiMessage(DinosaurEntity entity) {
        this.entity = entity;
        this.entityId = entity.getEntityId();
    }

    @Override
    public void onClientReceived(Minecraft minecraft, OpenFieldGuideGuiMessage message, EntityPlayer player, MessageContext context) {
        Entity entity = player.world.getEntityByID(message.entityId);

        if (entity instanceof DinosaurEntity) {
            DinosaurEntity dinosaur = (DinosaurEntity) entity;

            JurassiCraft.PROXY.openFieldGuide(dinosaur, message.fieldGuideInfo);
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, OpenFieldGuideGuiMessage message, EntityPlayer player, MessageContext context) {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.fieldGuideInfo = DinosaurEntity.FieldGuideInfo.deserialize(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        DinosaurEntity.FieldGuideInfo.serialize(buf, this.entity);
    }
}
