package org.jurassicraft.server.entity.ai;

import net.minecraft.world.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.RayTraceResult;
import org.jurassicraft.client.model.animation.EntityAnimation;
import org.jurassicraft.server.entity.DinosaurEntity;

public class RaptorLeapEntityAI extends EntityAIBase {
    private DinosaurEntity entity;
    private EntityLivingBase target;

    private int prevTick;
    private EntityAnimation animation;

    private double targetPrevPosX;
    private double targetPrevPosZ;

    private boolean ticked = false;

    public RaptorLeapEntityAI(DinosaurEntity entity) {
        this.entity = entity;
        this.setMutexBits(Mutex.ATTACK | Mutex.ANIMATION);
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.herd != null && this.entity.herd.fleeing) {
            return false;
        }

        EntityLivingBase target = this.entity.getAttackTarget();

        if (target != null && target.isEntityAlive() && !(target instanceof DinosaurEntity && ((DinosaurEntity) target).isCarcass())) {
            float distance = this.entity.getDistance(target);

            if (distance >= 5 && distance <= 6 && this.entity.onGround) {
                RayTraceResult result = this.entity.world.rayTraceBlocks(this.entity.position().addVector(0.0, 1.0, 0.0), target.position(), false, true, false);
                if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) {
                    this.target = target;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void startExecuting() {
        this.animation = EntityAnimation.PREPARE_LEAP;
        this.entity.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);
        this.ticked = false;
    }

    @Override
    public void updateTask() {
        int tick = this.entity.getAnimationTick();

        if (this.animation == EntityAnimation.PREPARE_LEAP && tick < this.prevTick) {
            this.animation = EntityAnimation.LEAP;

            this.entity.playSound(this.entity.getSoundForAnimation(EntityAnimation.ATTACKING.get()), this.entity.getSoundVolume(), this.entity.getSoundPitch());

            double targetSpeedX = this.target.getX() - (!this.ticked ? this.target.prevPosX : this.targetPrevPosX);
            double targetSpeedZ = this.target.getZ() - (!this.ticked ? this.target.prevPosZ : this.targetPrevPosZ);

            double length = this.entity.width * 6.0F;

            double destX = this.target.getX() + targetSpeedX * length;
            double destZ = this.target.getZ() + targetSpeedZ * length;

            double delta = Math.sqrt((destX - this.entity.getX()) * (destX - this.entity.getX()) + (destZ - this.entity.getZ()) * (destZ - this.entity.getZ()));
            double angle = Math.atan2(destZ - this.entity.getZ(), destX - this.entity.getX());

            this.entity.motionX = delta / length * Math.cos(angle);
            this.entity.motionZ = (delta / length * Math.sin(angle));
            this.entity.motionY = Math.min(0.3, Math.max(0, (this.target.getY() - this.entity.getY()) * 0.1)) + 0.6;
        } else if (this.animation == EntityAnimation.LEAP && this.entity.motionY < 0) {
            this.animation = EntityAnimation.LEAP_LAND;
        } else if (this.animation == EntityAnimation.LEAP_LAND && (this.entity.onGround || this.entity.isSwimming())) {
            this.animation = EntityAnimation.IDLE;

            if (this.entity.getEntityBoundingBox() != null && this.target.getEntityBoundingBox() != null && this.entity.getEntityBoundingBox().intersects(this.target.getEntityBoundingBox().expand(2.0, 2.0, 2.0))) {
                this.entity.attackEntityAsMob(this.target);
            }
        }

        this.targetPrevPosX = this.target.getX();
        this.targetPrevPosZ = this.target.getZ();
        this.ticked = true;

        if (this.entity.getAnimation() != this.animation.get()) {
            this.entity.setAnimation(this.animation.get());
            this.entity.setAnimationTick(this.prevTick + 1);
        }

        this.prevTick = tick;
    }

    @Override
    public void resetTask() {
        this.entity.setAnimation(EntityAnimation.IDLE.get());
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.target.isRemoved() && !(this.target instanceof DinosaurEntity && ((DinosaurEntity) this.target).isCarcass()) && this.animation != EntityAnimation.IDLE;
    }
}
