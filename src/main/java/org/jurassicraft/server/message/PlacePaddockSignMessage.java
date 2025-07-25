package org.jurassicraft.server.message;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.entity.item.PaddockSignEntity;
import org.jurassicraft.server.item.ItemHandler;

public class PlacePaddockSignMessage extends AbstractMessage<PlacePaddockSignMessage> {
    private int dino;
    private BlockPos pos;
    private int x;
    private int y;
    private int z;
    private EnumFacing facing;
    private EnumHand hand;

    public PlacePaddockSignMessage() {
    }

    public PlacePaddockSignMessage(EnumHand hand, EnumFacing facing, BlockPos pos, Dinosaur dino) {
        this.dino = EntityHandler.getDinosaurId(dino);
        this.pos = new BlockPos(this.x, this.y, this.z);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.facing = facing;
        this.hand = hand;
    }

    @Override
    public void onClientReceived(Minecraft minecraft, PlacePaddockSignMessage message, EntityPlayer player, MessageContext messageContext) {
    }

    @Override
    public void onServerReceived(MinecraftServer server, PlacePaddockSignMessage message, EntityPlayer player, MessageContext messageContext) {
        World world = player.world;

        EnumFacing side = message.facing;
        BlockPos pos = message.pos;

        PaddockSignEntity paddockSign = new PaddockSignEntity(world, pos, side, message.dino);

        ItemStack heldItem = player.getHeldItem(message.hand);

        if (heldItem != null && heldItem.getItem() == ItemHandler.PADDOCK_SIGN) {
            if (player.canPlayerEdit(pos, side, heldItem) && paddockSign.onValidSurface()) {
                world.spawnEntity(paddockSign);

                if (!player.capabilities.isCreativeMode) {
                    InventoryPlayer inventory = player.inventory;
                    inventory.decrStackSize(inventory.currentItem, 1);
                }
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeInt(this.dino);
        buffer.writeByte((byte) this.facing.getIndex());
        buffer.writeByte((byte) this.hand.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.dino = buffer.readInt();
        this.facing = EnumFacing.getFront(buffer.readByte());
        this.hand = EnumHand.values()[buffer.readByte()];
        this.pos = new BlockPos(this.x, this.y, this.z);
    }
}
