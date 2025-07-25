package org.jurassicraft.server.entity.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityHanging;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.server.item.ItemHandler;

public class PaddockSignEntity extends EntityHanging implements IEntityAdditionalSpawnData {
    private int dinosaur;

    public PaddockSignEntity(World world) {
        super(world);
    }

    public PaddockSignEntity(World world, BlockPos pos, EnumFacing enumFacing, int dinosaur) {
        super(world, pos);
        this.setType(dinosaur);

        this.updateFacingWithBoundingBox(enumFacing);
    }

    private void setType(int dinosaur) {
        this.dinosaur = dinosaur;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("Dinosaur", this.dinosaur);
        super.writeEntityToNBT(tagCompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.setType(tagCompund.getInteger("Dinosaur"));

        super.readEntityFromNBT(tagCompund);
    }

    @Override
    public int getWidthPixels() {
        return 16;
    }

    @Override
    public int getHeightPixels() {
        return 16;
    }

    @Override
    public void onBroken(Entity entity) {
        if (this.world.getGameRules().getBoolean("doTileDrops")) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entity;

                if (entityplayer.capabilities.isCreativeMode) {
                    return;
                }
            }

            ItemStack stack = new ItemStack(ItemHandler.PADDOCK_SIGN);
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("Dinosaur", this.dinosaur);

            this.entityDropItem(stack, 0.0F);
        }
    }

    @Override
    public void playPlaceSound() {
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
    }

    @Override
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
        BlockPos positionOffset = new BlockPos(x - this.getX(), y - this.getY(), z - this.getZ());
        BlockPos newPosition = this.hangingPosition.add(positionOffset);
        this.setPosition(newPosition.getX(), newPosition.getY(), newPosition.getZ());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.dinosaur);
        buffer.writeLong(this.hangingPosition.toLong());
        buffer.writeByte(this.facingDirection.getHorizontalIndex());
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        this.setType(buf.readInt());
        this.hangingPosition = BlockPos.fromLong(buf.readLong());
        this.updateFacingWithBoundingBox(EnumFacing.getHorizontal(buf.readByte()));
    }

    public int getDinosaur() {
        return this.dinosaur;
    }
}
