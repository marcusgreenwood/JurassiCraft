package org.jurassicraft.server.message;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemRecord;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.client.sound.EntitySound;
import org.jurassicraft.server.entity.vehicle.VehicleEntity;

public class CarEntityPlayRecord extends AbstractMessage<CarEntityPlayRecord> {

    private int entityId;
    private ItemStack record;

    public CarEntityPlayRecord(){}

    public CarEntityPlayRecord(VehicleEntity entity, ItemStack record){
        this.record = record;
        this.entityId = entity.getEntityId();
    }


    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        ByteBufUtils.writeItemStack(buf, this.record);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        record = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void onClientReceived(Minecraft client, CarEntityPlayRecord message, EntityPlayer player, MessageContext messageContext) {
        Entity entity = player.world.getEntityByID(message.entityId);
        if(entity instanceof VehicleEntity) {
            VehicleEntity carEntity = (VehicleEntity)entity;
            if(carEntity.sound != null) {
                carEntity.sound.setFinished();
            }
            carEntity.sound = new EntitySound<>(carEntity, ((ItemRecord) message.record.getItem()).getSound(), SoundCategory.RECORDS, car -> car.getItem().getItem() instanceof ItemRecord && ((ItemRecord)car.getItem().getItem()).getSound() == ((ItemRecord) message.record.getItem()).getSound());
            client.getSoundHandler().playSound(carEntity.sound);
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, CarEntityPlayRecord message, EntityPlayer player, MessageContext messageContext) {}
}
