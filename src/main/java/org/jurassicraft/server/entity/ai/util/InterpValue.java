package org.jurassicraft.server.entity.ai.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jurassicraft.JurassiCraft;

// TODO: This class needs complete rewrite for NeoForge event system
public class InterpValue implements INBTSerializable<CompoundTag> {
    private double current;
    private double target;
    private final double speed;
    private final Entity parent;
    private static final List<InterpValue> CLIENT_VALUES = new CopyOnWriteArrayList<>();
    private static final List<InterpValue> SERVER_VALUES = new CopyOnWriteArrayList<>();

    public InterpValue(Entity entity, double speed) {
        this.parent = entity;
        this.speed = speed;
        if (entity != null) {
            if (entity.level().isClientSide) {
                CLIENT_VALUES.add(this);
            } else {
                SERVER_VALUES.add(this);
            }
        }
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public double getCurrent() {
        return current;
    }

    public double getTarget() {
        return target;
    }

    public double getSpeed() {
        return speed;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public void setCurrentTarget(double target) {
        this.current = target;
        this.target = target;
    }

    public boolean isDead() {
        return parent == null || !parent.isAlive();
    }

    public void update() {
        if (Math.abs(current - target) < 0.001) {
            current = target;
        } else {
            if (current < target) {
                current += Math.min(target - current, speed);
            } else {
                current -= Math.min(current - target, speed);
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("current", current);
        tag.putDouble("target", target);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        current = nbt.getDouble("current");
        target = nbt.getDouble("target");
    }

    // TODO: Fix event handling for NeoForge
    /*
    @SubscribeEvent
    public static void onTick(LevelTickEvent event) {
        // This needs to be rewritten for the new event system
    }
    */
}