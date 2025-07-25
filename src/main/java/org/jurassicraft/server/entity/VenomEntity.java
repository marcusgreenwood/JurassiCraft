package org.jurassicraft.server.entity;

import net.minecraft.world.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.level.Level;
import org.jurassicraft.client.proxy.ClientProxy;
import org.jurassicraft.server.entity.dinosaur.DilophosaurusEntity;

public class VenomEntity extends EntityThrowable {
    public VenomEntity(World world) {
        super(world);

        if (world.isRemote) {
            this.spawnParticles();
        }
    }

    public VenomEntity(World world, DilophosaurusEntity entity) {
        super(world, entity);

        if (world.isRemote) {
            this.spawnParticles();
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        EntityLivingBase thrower = this.getThrower();

        if (thrower instanceof DilophosaurusEntity) {
            DilophosaurusEntity spitter = (DilophosaurusEntity) thrower;

            if (result.entityHit != null && result.entityHit instanceof EntityLivingBase && result.entityHit != spitter && (result.entityHit == spitter.getAttackTarget() || !(result.entityHit instanceof DilophosaurusEntity))) {
                EntityLivingBase entityHit = (EntityLivingBase) result.entityHit;

                entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 4.0F);
                entityHit.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 300, 1, false, false));
                entityHit.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 300, 1, false, false));

                if (!this.level().isClientSide) {
                    this.discard();
                }
            }
        }
    }

    private void spawnParticles() {
        ClientProxy.spawnVenomParticles(this);
    }
}
