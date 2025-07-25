package org.jurassicraft.server.entity.dinosaur;

import net.ilexiconn.llibrary.server.animation.Animation;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.event.ClientEventHandler;
import org.jurassicraft.client.model.animation.EntityAnimation;
import org.jurassicraft.client.proxy.ClientProxy;
import org.jurassicraft.client.sound.SoundHandler;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.ai.LeapingMeleeEntityAI;
import org.jurassicraft.server.entity.ai.RaptorClimbTreeAI;
import org.jurassicraft.server.entity.ai.RaptorLeapEntityAI;
import org.jurassicraft.server.entity.ai.animations.BirdPreenAnimationAI;
import org.jurassicraft.server.entity.ai.animations.TailDisplayAnimationAI;
import org.jurassicraft.server.message.MicroraptorDismountMessage;

public class MicroraptorEntity extends DinosaurEntity {
    private int flyTime;
    private int groundHeight;
    private Vec3d glidingPos;

    public MicroraptorEntity(World world) {
        super(world);
        this.target(EntityChicken.class, EntityRabbit.class);
        this.tasks.addTask(1, new LeapingMeleeEntityAI(this, this.dinosaur.getMetadata().getAttackSpeed()));
        this.tasks.addTask(2, new RaptorClimbTreeAI(this, 1.0f));
        this.animationTasks.addTask(3, new BirdPreenAnimationAI(this));
        this.animationTasks.addTask(3, new TailDisplayAnimationAI(this));
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        return damageSource != DamageSource.FLY_INTO_WALL && super.attackEntityFrom(damageSource, amount);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
    }


    @Override
    public void onUpdate() {
        super.onUpdate();
        Animation curAni = this.getAnimation();
        boolean climbing = curAni == EntityAnimation.CLIMBING.get() || curAni == EntityAnimation.START_CLIMBING.get();

        if (climbing) {
            BlockPos trunk = new BlockPos(this.getX(), this.getEntityBoundingBox().minY, this.getZ());
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                if (!world.isAirBlock(trunk.offset(facing)) && this.world.isBlockFullCube(trunk.offset(facing))) {
                    this.rotationYawHead = this.prevRotationYawHead = this.rotationYaw = this.prevRotationYaw = facing.getHorizontalAngle();
                    this.renderYawOffset = this.prevRenderYawOffset = this.rotationYaw;
                    this.newPosRotationIncrements = 0;
                    break;
                }
            }
        }
    }
    
    @Override
    public void travel(float strafe, float vertical, float forward) {
	float prevRot = this.rotationPitch;
        if(this.getAnimation() == EntityAnimation.GLIDING.get() && glidingPos != null) {
            this.rotationPitch = (float) -Math.toDegrees(Math.asin((this.glidingPos.y - this.getY()) / glidingPos.distanceTo(this.position())));
        }
        super.travel(strafe, vertical, forward);
        this.rotationPitch = prevRot;
    }

    @Override
    public void onLivingUpdate() {
        
        super.onLivingUpdate();

        
        if (this.level().isClientSide) {
            this.updateClientControls();
        }
        Animation curAni = this.getAnimation();
        boolean landing = curAni == EntityAnimation.LEAP_LAND.get();
        boolean gliding = curAni == EntityAnimation.GLIDING.get();
        boolean climbing = curAni == EntityAnimation.CLIMBING.get() || curAni == EntityAnimation.START_CLIMBING.get();
        boolean leaping = curAni == EntityAnimation.LEAP.get();

        if (this.onGround || this.inWater() || this.inLava() || this.isSwimming()) {
            this.flyTime = 0;
            if (gliding || landing) {
                this.setAnimation(EntityAnimation.IDLE.get());
                this.setFlag(7, false);
            }
        } else {
            this.flyTime++;
            if (this.flyTime > 4 && !leaping) {
                if (!landing) {
                    if (!gliding) {
                        if (!climbing) {
                            this.setAnimation(EntityAnimation.GLIDING.get());
                        }
                    } else if (!this.world.isAirBlock(this.getPosition().down())) {
                        this.setAnimation(EntityAnimation.LEAP_LAND.get());
                    }
                }
                if (gliding) {
                    this.setFlag(7, true);
                }
            }
        }

        if (this.isElytraFlying()) {
            this.groundHeight = 0;
            BlockPos pos = this.getPosition();
            while (this.groundHeight <= 10) {
                if (this.world.isSideSolid(pos, Direction.UP, true)) {
                    break;
                }
                pos = pos.down();
                this.groundHeight++;
            }
        }

        if (this.isServerWorld()) {
            this.getLookHelper().onUpdateLook();
        }
    }

    @Override
    public EntityLookHelper getLookHelper() {
        return super.getLookHelper();
    }

    @Override
    public Vec3d getLookVec() {
        if (this.getAnimation() == EntityAnimation.GLIDING.get() && glidingPos != null) {
            double distance = glidingPos.distanceTo(this.position());
            return new Vec3d((glidingPos.x - this.getX()) / distance, (this.glidingPos.y - this.getY()) / distance, (this.glidingPos.z - this.getZ()) / distance);
        }
        return super.getLookVec();
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (player.isSneaking() && hand == InteractionHand.MAIN_HAND) {
            if (this.isOwner(player) && this.order == Order.SIT && player.getPassengers() != null && player.getPassengers().size() < 2) {
                return this.startRiding(player, true);
            }
        }
        return super.processInteract(player, hand);
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slot) {
        if (this.getAnimation() == EntityAnimation.GLIDING.get() && slot == EntityEquipmentSlot.CHEST) {
            return new ItemStack(Items.ELYTRA);
        }
        return super.getItemStackFromSlot(slot);
    }

    @Override
    public EntityAIBase getAttackAI() {
        return new RaptorLeapEntityAI(this);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        if (this.getAnimation() != EntityAnimation.LEAP_LAND.get()) {
            super.fall(distance / 2.0F, damageMultiplier);
        }
    }

    @Override
    protected PathNavigate createNavigator(World worldIn) {
        return new PathNavigateClimber(this, worldIn);
    }

    @Override
    public SoundEvent getSoundForAnimation(Animation animation) {
        switch (EntityAnimation.getAnimation(animation)) {
            case SPEAK:
                return SoundHandler.MICRORAPTOR_LIVING;
            case DYING:
                return SoundHandler.MICRORAPTOR_DEATH;
            case INJURED:
                return SoundHandler.MICRORAPTOR_HURT;
            case ATTACKING:
                return SoundHandler.MICRORAPTOR_ATTACK;
            case CALLING:
                return SoundHandler.MICRORAPTOR_LIVING;
		default:
			break;
        }

        return null;
    }

    @Override
    public int getMaxFallHeight() {
        return 100;
    }

    @Override
    public void updateRidden() {
        Entity entity = this.getRidingEntity();
        if (this.isRiding() && entity.isRemoved()) {
            this.dismountRidingEntity();
        } else {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.onUpdate();
            if (this.isRiding()) {
                this.updateRiding(entity);
            }
        }
    }

    private void updateRiding(Entity riding) {
        if (riding.isPassenger(this) && riding instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) riding;
            int ridingIndex = riding.getPassengers().indexOf(this);
            float radius = (ridingIndex == 2 ? 0.0F : 0.35F) + (player.isElytraFlying() ? 2 : 0);
            float renderYawOffset = player.renderYawOffset;
            float angle = (float) Math.toRadians(renderYawOffset + (ridingIndex == 1 ? -90 : ridingIndex == 0 ? 90 : 0));
            double offsetX = (double) (radius * MathHelper.sin((float) (Math.PI + angle)));
            double offsetZ = (double) (radius * MathHelper.cos(angle));
            double offsetY = (riding.isSneaking() ? 1.2 : 1.38) + (ridingIndex == 2 ? 0.4 : 0.0);
            this.rotationYaw = player.rotationYawHead;
            this.rotationYawHead = player.rotationYawHead;
            this.prevRotationYaw = player.rotationYawHead;
            this.setPosition(riding.getX() + offsetX, riding.getY() + offsetY, riding.getZ() + offsetZ);
            this.setAnimation(EntityAnimation.IDLE.get());
            if (player.isElytraFlying()) {
                this.dismountRidingEntity();
            }
        }
    }

    protected void updateClientControls() {
        if (this.getRidingEntity() != null && this.getRidingEntity() == ClientProxy.MC.player) {
            if (ClientProxy.getKeyHandler().MICRORAPTOR_DISMOUNT.isKeyDown()) {
                JurassiCraft.NETWORK_WRAPPER.sendToServer(new MicroraptorDismountMessage(this.getEntityId()));
            }
        }
    }
    
    @Override
    public boolean canDinoSwim() {
        return false;
    }
    
	@Override
	public boolean shouldEscapeWaterFast() {
		int radiusXZ = 4;

		for (BlockPos pos : BlockPos.getAllInBox(MathHelper.floor(this.getX() - radiusXZ), MathHelper.floor(this.getY()),
				MathHelper.floor(this.getZ() - radiusXZ), MathHelper.ceil(this.getX() + radiusXZ),
				MathHelper.ceil(this.getY()), MathHelper.ceil(this.getZ() + radiusXZ))) {
			if (!world.getBlockState(pos).getMaterial().isLiquid()) {
				return false;
			}
		}
		return false;
	}
    
    public void setGlidingTo(Vec3d glidingPos) {
	this.glidingPos = glidingPos;
    }
}
