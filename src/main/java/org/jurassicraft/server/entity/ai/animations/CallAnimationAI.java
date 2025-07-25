package org.jurassicraft.server.entity.ai.animations;

import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.phys.AABB;
import org.jurassicraft.client.model.animation.EntityAnimation;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.ai.Mutex;

import java.util.List;

public class CallAnimationAI extends EntityAIBase {
    protected DinosaurEntity dinosaur;

    public CallAnimationAI(IAnimatedEntity entity) {
        super();
        this.dinosaur = (DinosaurEntity) entity;
        this.setMutexBits(Mutex.ANIMATION);
    }

    public List<Entity> getEntitiesWithinDistance(Entity entity, double width, double height) {
        return entity.world.getEntitiesWithinAABBExcludingEntity(entity, new AxisAlignedBB(entity.getX() - width, entity.getY() - height, entity.getZ() - width, entity.getX() + width, entity.getY() + height, entity.getZ() + width));
    }

    @Override
    public boolean shouldExecute() {
        if (!this.dinosaur.isBusy() && this.dinosaur.getRNG().nextDouble() < 0.003) {
            List<Entity> entities = this.getEntitiesWithinDistance(this.dinosaur, 50, 10);

            for (Entity entity : entities) {
                if (this.dinosaur.getClass().isInstance(entity)) {
                    this.dinosaur.playSound(this.dinosaur.getSoundForAnimation(EntityAnimation.CALLING.get()), this.dinosaur.getSoundVolume() > 0.0F ? this.dinosaur.getSoundVolume() + 1.25F : 0.0F, this.dinosaur.getSoundPitch());
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void startExecuting() {
        this.dinosaur.setAnimation(EntityAnimation.CALLING.get());
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }
}
