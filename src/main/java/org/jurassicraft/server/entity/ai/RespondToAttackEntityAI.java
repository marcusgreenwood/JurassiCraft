package org.jurassicraft.server.entity.ai;

import net.minecraft.world.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.math.Vec3i;
import org.jurassicraft.server.entity.DinosaurEntity;

public class RespondToAttackEntityAI extends EntityAIBase {
    private DinosaurEntity dinosaur;
    private EntityLivingBase attacker;

    public RespondToAttackEntityAI(DinosaurEntity dinosaur) {
        this.dinosaur = dinosaur;
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        this.attacker = this.dinosaur.getAttackTarget();
        if(this.attacker != null) {
            return this.dinosaur.canEntityBeSeen(this.attacker) && !this.attacker.isRemoved() && !(this.attacker instanceof DinosaurEntity && ((DinosaurEntity) this.attacker).isCarcass()) && !(this.attacker instanceof EntityPlayer && ((EntityPlayer) this.attacker).capabilities.isCreativeMode);
        }
        return false;
    }

    @Override
    public void startExecuting() {
        this.dinosaur.respondToAttack(this.attacker);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }
}
