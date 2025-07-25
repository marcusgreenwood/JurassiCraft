package org.jurassicraft.server.entity.dinosaur;

import net.ilexiconn.llibrary.server.animation.Animation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.level.Level;
import org.jurassicraft.client.model.animation.EntityAnimation;
import org.jurassicraft.client.sound.SoundHandler;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.LegSolver;
import org.jurassicraft.server.entity.LegSolverQuadruped;

public class BrachiosaurusEntity extends DinosaurEntity {
    private int stepCount = 0;

    public LegSolverQuadruped legSolver;

    public BrachiosaurusEntity(World world) {
        super(world);
    }

    @Override
    protected LegSolver createLegSolver() {
        return this.legSolver = new LegSolverQuadruped(2.5F, 2.0F, 1.0F, 1.0F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.onGround && !this.isInWater()) {
            if (this.moveForward > 0 && (this.getX() - this.prevPosX > 0 || this.getZ() - this.prevPosZ > 0) && this.stepCount <= 0) {
                this.playSound(SoundHandler.STOMP, (float) this.interpolate(0.1F, 1.0F), this.getSoundPitch());
                this.stepCount = 65;
            }
            this.stepCount -= this.moveForward * 9.5;
        }
    }

    @Override
    public SoundEvent getSoundForAnimation(Animation animation) {
        switch (EntityAnimation.getAnimation(animation)) {
            case SPEAK:
                return SoundHandler.BRACHIOSAURUS_LIVING;
            case CALLING:
                return SoundHandler.BRACHIOSAURUS_LIVING;
            case DYING:
                return SoundHandler.BRACHIOSAURUS_DEATH;
            case INJURED:
                return SoundHandler.BRACHIOSAURUS_HURT;
            default:
                return null;
        }
    }
}
