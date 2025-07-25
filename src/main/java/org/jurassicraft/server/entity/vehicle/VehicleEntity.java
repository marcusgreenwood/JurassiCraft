package org.jurassicraft.server.entity.vehicle;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.entity.vehicle.util.WheelParticleData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VehicleEntity extends Entity {
    
    // Basic data parameters for syncing
    private static final EntityDataAccessor<Float> HEALTH = 
        SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.FLOAT);
    
    // Wheel data for tire tracks
    public final List<List<WheelParticleData>> wheelDataList = Lists.newArrayList();
    
    // Basic vehicle properties
    protected float health = 100.0F;
    protected boolean canBreak = true;
    
    public VehicleEntity(EntityType<? extends VehicleEntity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(false);
        
        // Initialize wheel data lists
        for (int i = 0; i < 4; i++) {
            wheelDataList.add(Lists.newArrayList());
        }
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.define(HEALTH, 100.0F);
    }
    
    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        this.health = compound.getFloat("Health");
    }
    
    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        compound.putFloat("Health", this.health);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Update health sync
        this.entityData.set(HEALTH, this.health);
        
        // Basic physics - let Minecraft handle most of it
        if (!this.level().isClientSide) {
            this.updateMovement();
        }
        
        // Update wheel particle data
        this.updateWheelData();
    }
    
    protected void updateMovement() {
        // Basic gravity and collision
        Vec3 motion = this.getDeltaMovement();
        
        // Apply gravity
        if (!this.isNoGravity()) {
            motion = motion.add(0, -0.08, 0);
        }
        
        // Basic ground collision
        BlockPos groundPos = this.blockPosition().below();
        BlockState groundState = this.level().getBlockState(groundPos);
        if (!groundState.isAir()) {
            if (motion.y < 0 && this.getY() - groundPos.getY() < 1.5) {
                motion = new Vec3(motion.x * 0.8, Math.max(0, motion.y), motion.z * 0.8);
            }
        }
        
        this.setDeltaMovement(motion);
        this.move(net.minecraft.world.entity.MoverType.SELF, motion);
    }
    
    protected void updateWheelData() {
        // Simple wheel data update - just clean up old data
        for (List<WheelParticleData> wheelList : wheelDataList) {
            List<WheelParticleData> toRemove = Lists.newArrayList();
            for (WheelParticleData data : wheelList) {
                data.onUpdate(toRemove);
            }
            wheelList.removeAll(toRemove);
        }
    }
    
    @Override
    public boolean hurt(@Nonnull DamageSource damageSource, float amount) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        
        this.health -= amount;
        
        if (this.health <= 0 && canBreak) {
            this.destroy();
            return true;
        }
        
        return true;
    }
    
    protected void destroy() {
        // Upload wheel data for rendering
        if (this.level().isClientSide) {
            // This would be handled by TyretrackRenderer
            // TyretrackRenderer.uploadList(this);
        }
        
        this.discard();
    }
    
    @Override
    @Nonnull
    public InteractionResult interact(@Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!this.level().isClientSide) {
            // Handle player interaction (enter vehicle, etc.)
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    
    // Utility methods
    public float getHealth() {
        return this.health;
    }
    
    public void setHealth(float health) {
        this.health = Math.max(0, health);
    }
    
    public boolean canBreak() {
        return this.canBreak;
    }
    
    public void setCanBreak(boolean canBreak) {
        this.canBreak = canBreak;
    }
    
    @Override
    public boolean canBeCollidedWith() {
        return !this.isRemoved();
    }
    
    @Override
    public boolean isPushable() {
        return true;
    }
    
    // Stub methods that were in the original for compatibility
    public void addWheelData(int wheelIndex, Vec3 position, Vec3 oppositePosition) {
        if (wheelIndex >= 0 && wheelIndex < wheelDataList.size()) {
            long worldTime = this.level().getGameTime();
            WheelParticleData data = new WheelParticleData(position, oppositePosition, worldTime);
            wheelDataList.get(wheelIndex).add(data);
            
            // Limit size to prevent memory issues
            List<WheelParticleData> wheelList = wheelDataList.get(wheelIndex);
            if (wheelList.size() > 1000) {
                wheelList.remove(0);
            }
        }
    }
}
