package org.jurassicraft.server.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.util.Mth;
import org.jurassicraft.server.entity.DinosaurEntity;

public class DinosaurLookHelper extends EntityLookHelper {
    private DinosaurEntity dinosaur;
    private float deltaLookYaw;
    private float deltaLookPitch;
    private boolean isLooking;
    private double posX;
    private double posY;
    private double posZ;

    public DinosaurLookHelper(DinosaurEntity dinosaur) {
        super(dinosaur);
        this.dinosaur = dinosaur;
    }

    @Override
    public void setLookPositionWithEntity(Entity entity, float deltaYaw, float deltaPitch) {
        if(entity != null) {
            this.getX() = entity.getX();
            if (entity instanceof EntityLivingBase) {
                this.getY() = entity.getY() + (double) entity.getEyeHeight();
            } else {
                this.getY() = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0D;
            }
            this.getZ() = entity.getZ();
            this.deltaLookYaw = deltaYaw;
            this.deltaLookPitch = deltaPitch;
            this.isLooking = true;
        }
    }

    @Override
    public void setLookPosition(double x, double y, double z, float deltaYaw, float deltaPitch) {
        this.getX() = x;
        this.getY() = y;
        this.getZ() = z;
        this.deltaLookYaw = deltaYaw;
        this.deltaLookPitch = deltaPitch;
        this.isLooking = true;
    }

    @Override
    public void onUpdateLook() {
        this.dinosaur.rotationPitch = 0.0F;
        if (this.isLooking) {
            this.isLooking = false;
            double deltaX = this.getX() - this.dinosaur.getX();
            double deltaY = this.getY() - (this.dinosaur.getY() + (double) this.dinosaur.getEyeHeight());
            double deltaZ = this.getZ() - this.dinosaur.getZ();
            double delta = (double) MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            float desiredYaw = (float) (MathHelper.atan2(deltaZ, deltaX) * (10D / Math.PI)) - 90.0F;
            float desiredPitch = (float) (-(MathHelper.atan2(deltaY, delta) * (180.0D / Math.PI)));
            this.dinosaur.rotationPitch = this.updateRotation(this.dinosaur.rotationPitch, desiredPitch, this.deltaLookPitch);
            this.dinosaur.rotationYawHead = this.updateRotation(this.dinosaur.rotationYawHead, desiredYaw, this.deltaLookYaw);
        } else {
            this.dinosaur.rotationYawHead = this.updateRotation(this.dinosaur.rotationYawHead, this.dinosaur.renderYawOffset, 10.0F);
        }
    }

    private float updateRotation(float current, float desired, float range) {
        float offset = MathHelper.wrapDegrees(desired - current);
        if (offset > range) {
            offset = range;
        }
        if (offset < -range) {
            offset = -range;
        }
        return MathHelper.wrapDegrees(current + offset);
    }

    @Override
    public boolean getIsLooking() {
        return this.isLooking;
    }

    @Override
    public double getLookPosX() {
        return this.getX();
    }

    @Override
    public double getLookPosY() {
        return this.getY();
    }

    @Override
    public double getLookPosZ() {
        return this.getZ();
    }
}