package org.jurassicraft.server.entity.vehicle.util;

import java.util.List;

import org.jurassicraft.server.entity.vehicle.VehicleEntity;

import net.minecraft.world.phys.Vec3;

public class WheelParticleData {
    private int age;
    
    private final Vec3 position;
    private final Vec3 oppositePosition;
    private final int maxAge = 200;
    private final long worldTime;
    private boolean shouldRender = true;
    
    public WheelParticleData(Vec3 position, Vec3 oppositePosition, long worldTime) {
        this.position = position;
        this.oppositePosition = oppositePosition;
        this.worldTime = worldTime;
    }
    
    public WheelParticleData setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
        return this;
    }
    
    public boolean shouldRender() {
        return shouldRender;
    }
    
    public Vec3 getOppositePosition() {
        return oppositePosition;
    }
    
    public long getWorldTime() {
        return worldTime;
    }
    
    public void onUpdate(List<WheelParticleData> markedRemoved) {
        if (this.age++ >= this.maxAge) {
            markedRemoved.add(this);
        }
    }
    
    public Vec3 getPosition() {
        return position;
    }
    
    public float getAlpha(float partialTicks) {
        if(age > 199) {
            return 0f;
        }
        float f = (float) Math.pow(((double)this.age + partialTicks) / (double)this.maxAge, 2);
        float f1 = 2.0F - f * 2.0F;

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        f1 = f1 * 0.3F;
        return f1;
    }
}