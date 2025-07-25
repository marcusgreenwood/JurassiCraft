package org.jurassicraft.server.entity;

import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.event.ClientEventHandler;
import org.jurassicraft.client.proxy.ClientProxy;
import org.jurassicraft.server.item.Dart;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

public class TranquilizerDartEntity extends EntityThrowable implements IEntityAdditionalSpawnData {

    private ItemStack stack;
    
    public TranquilizerDartEntity(World worldIn) {
	super(worldIn);
    }
    
    public TranquilizerDartEntity(World worldIn, EntityLivingBase throwerIn, ItemStack stack) {
	super(worldIn, throwerIn);
	this.stack = stack.copy();
    }
    
    @Override
    public void onUpdate() {
	if(world.isRemote) {
	    spawnParticles();
	}
	super.onUpdate();
    }
    
    private void spawnParticles() {
    	ClientProxy.MC.effectRenderer.spawnEffectParticle(EnumParticleTypes.CLOUD.getParticleID(), this.getX() + this.motionX / 4.0D, this.getY() + this.motionY / 4.0D, this.getZ() + this.motionZ / 4.0D, 
    	    -this.motionX / 20.0D, 
    	    -this.motionY / 20.0D + 0.2D, 
    	    -this.motionZ / 20.0D);
    }

	@Override
	protected void onImpact(RayTraceResult result) {
		if (stack == null)
			return;
		Item item = stack.getItem();
		if (item != null && item instanceof Dart) {
			if (result.entityHit instanceof DinosaurEntity) {
				if (item instanceof Dart) {
					((Dart) item).getConsumer().accept((DinosaurEntity) result.entityHit, stack);
				} else {
					JurassiCraft.getLogger().error("Expected Dart Item, got {} ", item.getRegistryName());
				}
			}
			if (!this.level().isClientSide) {
				this.world.setEntityState(this, (byte) 3);
				this.discard();
			}
		}
	}

    @Override
    public void writeSpawnData(ByteBuf buffer) {
	if(stack != null)
		ByteBufUtils.writeItemStack(buffer, stack);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
	if(stack != null)
		stack = ByteBufUtils.readItemStack(additionalData);
    }
    
}
