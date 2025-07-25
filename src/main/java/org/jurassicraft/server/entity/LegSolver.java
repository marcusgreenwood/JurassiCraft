package org.jurassicraft.server.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLivingBase;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class LegSolver {
    public final Leg[] legs;

    public LegSolver(Leg... legs) {
        this.legs = legs;
    }

    public final void update(EntityLivingBase entity, float scale) {
        this.update(entity, entity.renderYawOffset, scale);
    }

    public final void update(Entity entity, float yaw, float scale) {
        double sideTheta = yaw / (180 / Math.PI);
        double sideX = Math.cos(sideTheta) * scale;
        double sideZ = Math.sin(sideTheta) * scale;
        double forwardTheta = sideTheta + Math.PI / 2;
        double forwardX = Math.cos(forwardTheta) * scale;
        double forwardZ = Math.sin(forwardTheta) * scale;
        for (Leg leg : this.legs) {
            leg.update(entity, sideX, sideZ, forwardX, forwardZ, scale);
        }
    }

    public static class Leg {
        public final float forward;

        public final float side;

        private final float range;

        private float height;

        private float prevHeight;

        public Leg(float forward, float side, float range) {
            this.forward = forward;
            this.side = side;
            this.range = range;
        }

        public final float getHeight(float delta) {
            return this.prevHeight + (this.height - this.prevHeight) * delta;
        }

        public void update(Entity entity, double sideX, double sideZ, double forwardX, double forwardZ, float scale) {
            this.prevHeight = this.height;
            float settledHeight = this.settle(entity, entity.getX() + sideX * this.side + forwardX * this.forward, entity.getY(), entity.getZ() + sideZ * this.side + forwardZ * this.forward, this.height);
            this.height = MathHelper.clamp(settledHeight, -this.range * scale, this.range * scale);
        }

        private float settle(Entity entity, double x, double y, double z, float height) {
            BlockPos pos = new BlockPos(x, y + 1e-3, z);
            float dist = this.getDistance(entity.world, pos);
            if (1 - dist < 1e-3) {
                dist = this.getDistance(entity.world, pos.down()) + (float) y % 1;
            } else {
                dist -= 1 - (y % 1);
            }
            if (entity.onGround && height <= dist) {
                return height == dist ? height : Math.min(height + this.getFallSpeed(), dist);
            } else if (height > 0) {
                return Math.max(height - this.getRiseSpeed(), dist);
            }
            return height;
        }

        private float getDistance(World world, BlockPos pos) {
            IBlockState state = world.getBlockState(pos);
            AxisAlignedBB aabb = state.getCollisionBoundingBox(world, pos);
            return aabb == null ? 1 : 1 - Math.min((float) aabb.maxY, 1);
        }

        protected float getFallSpeed() {
            return 0.3F;
        }

        protected float getRiseSpeed() {
            return 0.5F;
        }
    }
}
