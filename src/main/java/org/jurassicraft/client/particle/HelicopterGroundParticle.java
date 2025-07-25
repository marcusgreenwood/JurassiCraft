package org.jurassicraft.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

public class HelicopterGroundParticle extends Particle {
	float smokeParticleScale;

	public HelicopterGroundParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double velX, double velY, double velZ) {
		this(worldIn, xCoordIn, yCoordIn, zCoordIn, velX, velY, velZ, 1.0F);
	}

	protected HelicopterGroundParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i46348_8_, double p_i46348_10_, double p_i46348_12_, float p_i46348_14_) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
		this.motionX *= 0.10000000149011612D;
		this.motionY *= 0.10000000149011612D;
		this.motionZ *= 0.10000000149011612D;
		this.motionX += p_i46348_8_;
		this.motionY += p_i46348_10_;
		this.motionZ += p_i46348_12_;
		this.particleRed = 1.0f;
		this.particleGreen = 1.0f;
		this.particleBlue = 1.0f;
		this.particleScale *= 0.75F;
		this.particleScale *= p_i46348_14_;
		this.smokeParticleScale = this.particleScale;
		this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
		this.particleMaxAge = (int) ((float) this.particleMaxAge * p_i46348_14_);
	}

	/**
	 * Renders the particle
	 */
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float f = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge * 32.0F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		this.particleScale = this.smokeParticleScale * f;
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	public void onUpdate() {
		this.prevPosX = this.getX();
		this.prevPosY = this.getY();
		this.prevPosZ = this.getZ();

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}

		this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
		this.motionY += 0.004D;
		this.move(this.motionX, this.motionY, this.motionZ);

		if (this.getY() == this.prevPosY) {
			this.motionX *= 1.1D;
			this.motionZ *= 1.1D;
		}

		this.motionX *= 0.9599999785423279D;
		this.motionY *= 0.9599999785423279D;
		this.motionZ *= 0.9599999785423279D;

		if (this.onGround) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}
	}

	public static class Factory implements IParticleFactory {
		public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
			return new HelicopterGroundParticle(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		}
	}
}
