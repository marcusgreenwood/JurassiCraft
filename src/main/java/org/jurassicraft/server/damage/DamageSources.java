package org.jurassicraft.server.damage;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSources {
    public static final DamageSource SHOCK = new ShockDamageSource();
    public static final DamageSource CAR = new MultipleNameDamageSource("jurassic.car", 3);
}
