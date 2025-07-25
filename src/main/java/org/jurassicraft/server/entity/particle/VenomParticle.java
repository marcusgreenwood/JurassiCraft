package org.jurassicraft.server.entity.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.server.entity.VenomEntity;

public class VenomParticle extends Particle {
    private VenomEntity entity;
    private double offsetX;
    private double offsetY;
    private double offsetZ;

    public VenomParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale, VenomEntity entity) {
        super(world, entity.getX() + x, entity.getY() + y, entity.getZ() + z, 0.0D, 0.0D, 0.0D);
        this.entity = entity;
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.particleRed = this.particleGreen = this.particleBlue = (float) (Math.random() * 0.30000001192092896D);
        this.particleScale *= 0.75F;
        this.particleScale *= scale;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int) ((float) this.particleMaxAge * scale);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entity, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.getX();
        this.prevPosY = this.getY();
        this.prevPosZ = this.getZ();

        this.getX() = this.entity.getX() + this.offsetX;
        this.getY() = this.entity.getY() + this.offsetY;
        this.getZ() = this.entity.getZ() + this.offsetZ;

        if (this.entity.isRemoved()) {
            this.setExpired();
        }

        this.setParticleTextureIndex(7);
    }
}