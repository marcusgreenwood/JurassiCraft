package org.jurassicraft.server.entity.ai;

import net.minecraft.world.entity.EntityLivingBase;
import org.jurassicraft.server.entity.DinosaurEntity;

public class LeapingMeleeEntityAI extends DinosaurAttackMeleeEntityAI {
    public LeapingMeleeEntityAI(DinosaurEntity entity, double speed) {
        super(entity, speed, false);
        this.setMutexBits(Mutex.ATTACK);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.attacker.getAttackTarget();

        return super.shouldExecute() && target != null && this.isInRange(target);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute() && super.shouldContinueExecuting();
    }

    private boolean isInRange(EntityLivingBase target) {
        float distance = this.attacker.getDistance(target);
        float range = this.attacker.width * 6.0F;
        return distance < range - 1.0F || distance > range;
    }
}
