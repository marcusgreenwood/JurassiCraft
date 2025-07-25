package org.jurassicraft.server.entity.vehicle;

import com.google.common.collect.Lists;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.proxy.ClientProxy;
import org.jurassicraft.client.render.entity.TyretrackRenderer;
import org.jurassicraft.client.sound.EntitySound;
import org.jurassicraft.server.conf.JurassiCraftConfig;
import org.jurassicraft.server.damage.DamageSources;
import org.jurassicraft.server.entity.ai.util.InterpValue;
import org.jurassicraft.server.entity.vehicle.util.CarWheel;
import org.jurassicraft.server.entity.vehicle.util.WheelParticleData;
import org.jurassicraft.server.message.CarEntityPlayRecord;
import org.jurassicraft.server.message.UpdateVehicleControlMessage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;

public abstract class VehicleEntity extends Entity implements MultiSeatedEntity {
	public static final EntityDataAccessor<Byte> WATCHER_STATE = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.BYTE);
	public static final EntityDataAccessor<Float> WATCHER_HEALTH = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Integer> WATCHER_SPEED = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<ItemStack> RECORD_ITEM = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.ITEM_STACK);
	public static final EntityDataAccessor<CompoundTag> WATCHER_SEATS = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.COMPOUND_TAG);

	public static final float MAX_HEALTH = 40;
	private static final byte LEFT = 0b000001;
	private static final byte RIGHT = 0b000010;
	private static final byte FORWARD = 0b000100;
	private static final byte BACKWARD = 0b001000;

	protected final Seat[] seats = createSeats();
	protected final WheelData wheeldata = createWheels();

	public float wheelRotation;
	public float wheelRotateAmount;
	public float prevWheelRotateAmount;

	public float pitch;
	public float roll;
	protected float rotationDelta;
    private boolean noiseInstance;
	public int interpProgress;
	double interpTargetX;
	public double interpTargetY;
	public double interpTargetZ;
	public double interpTargetYaw;
	public float speedModifier = 0f;
	private static final double INTERP_AMOUNT = 0.15D; // TODO config ?
	public boolean wasOnGroundLastTick;
	private Vec3 prevUnairbornPos;

	public final InterpValue backValue = new InterpValue(this, INTERP_AMOUNT);
	public final InterpValue frontValue = new InterpValue(this, INTERP_AMOUNT);
	public final InterpValue leftValue = new InterpValue(this, INTERP_AMOUNT);
	public final InterpValue rightValue = new InterpValue(this, INTERP_AMOUNT);

	public final CarWheel backLeftWheel = new CarWheel(0, wheeldata.bl);
	public final CarWheel backRightWheel = new CarWheel(1, wheeldata.br);
	public final CarWheel frontLeftWheel = new CarWheel(2, wheeldata.fl);
	public final CarWheel frontRightWheel = new CarWheel(3, wheeldata.fr);

	public final List<WheelParticleData>[] wheelDataList = new List[4];

	public List<CarWheel> allWheels = Lists.newArrayList(backLeftWheel, frontLeftWheel, backRightWheel,
			frontRightWheel);

	@OnlyIn(Dist.CLIENT)
	public EntitySound<VehicleEntity> sound;
	@OnlyIn(Dist.CLIENT)
	public InterpValue steerAmount;

	private float healAmount;
	private int healCooldown = 40;

	private Vec3 previousPosition = null; // Used for speed calculations
	private long prevWorldTime = -1;// Also used for speed calculations

	public double estimatedSpeed = 0D;

	private byte prevState = 0;
	
//	public float tmpX=0;
//	public float tmpY=0;
//	public float tmpZ=0;

	public VehicleEntity(Level world) {
		super(JurassiCraft.VEHICLE_ENTITY_TYPE, world); // TODO: Fix entity type registration
		this.setBoundingBox(new AABB(-1.5, 0, -1.5, 1.5, 2.5, 1.5));
		this.maxUpStep = 1.5F;
		if (world.isClientSide) {
			this.steerAmount = new InterpValue(this, 0.1D);
		}
		for (int i = 0; i < 4; i++) {
			this.wheelDataList[i] = Lists.newArrayList();
		}

		backLeftWheel.setPair(backRightWheel);
		frontLeftWheel.setPair(frontRightWheel);

	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(WATCHER_STATE, (byte) 0);
		this.entityData.define(WATCHER_HEALTH, MAX_HEALTH);
		this.entityData.define(WATCHER_SPEED, 1);
		this.entityData.define(RECORD_ITEM, ItemStack.EMPTY);
		CompoundTag s = new CompoundTag();
		for (int i = 0; i < createSeats().length; i++) {
			s.putString(str(i), "");
		}
		this.entityData.define(WATCHER_SEATS, s);
	}

	public boolean left() {
		return this.getStateBit(LEFT);
	}

	public boolean right() {
		return this.getStateBit(RIGHT);
	}

	public boolean forward() {
		return this.getStateBit(FORWARD);
	}

	public boolean backward() {
		return this.getStateBit(BACKWARD);
	}

	public void left(boolean left) {
		this.setStateBit(LEFT, left);
	}

	public void right(boolean right) {
		this.setStateBit(RIGHT, right);
	}

	public void forward(boolean forward) {
		this.setStateBit(FORWARD, forward);
	}

	public void backward(boolean backward) {
		this.setStateBit(BACKWARD, backward);
	}

	protected boolean getStateBit(byte mask) {
		return (this.getControlState() & mask) != 0;
	}

	protected void setStateBit(byte mask, boolean newState) {
		byte state = this.getControlState();
		this.setControlState((byte) (newState ? state | mask : state & ~mask));
	}

	public byte getControlState() {

		return this.dataManager.get(WATCHER_STATE);

	}

	public byte getPreviousState() {

		return this.prevState;

	}

	public void setControlState(byte state) {

		this.dataManager.set(WATCHER_STATE, state);
	}

	public void setPreviousState(byte state) {
		this.prevState = state;
	}

	public void setSpeed(Speed speed) {
		this.dataManager.set(WATCHER_SPEED, speed.ordinal());
	}

	public Speed getSpeed() {
		return Speed.values()[this.dataManager.get(WATCHER_SPEED)];
	}

	public void setHealth(float health) {
		this.entityData.set(WATCHER_HEALTH, health);
	}

	public float getHealth() {
		return this.entityData.get(WATCHER_HEALTH);
	}

	public ItemStack getItem() {
		return this.entityData.get(RECORD_ITEM);
	}

	@Override
	public boolean isInRangeToRenderDist(double dist) {
		return true;
	}

	@Override
	protected boolean canFitPassenger(@Nonnull Entity passenger) {
		return this.getPassengers().size() < this.seats.length;
	}

	@Override
	public Entity getControllingPassenger() {
		String id = getIfExists(0, false);
		return id.equals("") ? null : this.level().getEntity(Integer.parseInt(id));
	}

	@Nullable
	@Override
	public Entity getEntityInSeat(int seatID) {

		String id = this.getIfExists(seatID, false);
		return id.equals("") ? null : this.level().getEntity(Integer.parseInt(id));
	}

	public String getIfExists(int seatID, boolean reset) {

		String string = this.entityData.get(WATCHER_SEATS).getString(str(seatID));
		if (!string.equals("")) {
			if (!(this.level().getEntity(Integer.parseInt(string)) != null
					&& this.level().getEntity(Integer.parseInt(string)).getVehicle() == this)) {
				if (reset)
					setSeat(str(seatID), "");
				return "";
			} else {
				return string;
			}
		}
		return "";

	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	public Object getCarDimensions() { // TODO: Fix Vector4d replacement
		return this.wheeldata.carVector;
	}

	public Object getBackWheelRotationPoint() { // TODO: Fix Vector2d replacement
		return null; // new Vector2d(-0.5, 1.4);
	}

	// TODO: This method needs to be updated for modern entity interpolation
	/*
	@Override
	@OnlyIn(Dist.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int duration,
			boolean teleport) {
		this.interpTargetX = x;
		this.interpTargetY = y;
		this.interpTargetZ = z;
		this.interpTargetYaw = yaw;
		this.interpProgress = duration;
	}
	*/

	private void resetFlyTicks(@Nonnull ServerPlayer entity) {
		entity.fallDistance = 0F;
		Field field = null;
		Field field2 = null;
		try {
			if ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
				field = NetHandlerPlayServer.class.getDeclaredField("vehicleFloatingTickCount");
				field2 = NetHandlerPlayServer.class.getDeclaredField("floatingTickCount");
			} else {
				field = NetHandlerPlayServer.class.getDeclaredField("field_184346_E");
				field2 = NetHandlerPlayServer.class.getDeclaredField("field_147365_f");
			}

			field.setAccessible(true);
			field.set(entity.connection, 0);
			field2.setAccessible(true);
			field2.set(entity.connection, 0);

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fall(float distance, float damageMultiplier) {

		if (!world.isRemote) {
			
			float damage = MathHelper.ceil((distance - 3F) * damageMultiplier);

			if (damage > 0) {
				JurassiCraft.getLogger().debug("The vehicle at the coordinates " + this.getPosition() + " fell down! " + distance + "   " + damage);
				this.setHealth(this.getHealth() - (float) (damage * 1.25F));

				if (this.getHealth() < 0) {
					this.setDead();

					if (this.world.getGameRules().getBoolean("doEntityDrops")) {

						this.dropItems();
						ItemStack recordItem = this.dataManager.get(RECORD_ITEM);
						if (!recordItem.isEmpty()) {
							this.entityDropItem(recordItem, 0);
						}
					}
				}
			}
		}
	}

	@Override
	public void onEntityUpdate() {
		
		if (!world.isRemote) {
			if (this.getHealth() < 0 && !this.isDead) {
				this.setDead();
				if (this.world.getGameRules().getBoolean("doEntityDrops")) {
					this.dropItems();
					ItemStack recordItem = this.dataManager.get(RECORD_ITEM);
					if (!recordItem.isEmpty()) {
						this.entityDropItem(recordItem, 0);
					}
				}
			}

		}
		
		for (int i = 0; i < this.seats.length; i++) {
			Entity e = this.getEntityInSeat(i);
			if(e != null && e instanceof EntityPlayerMP) {
    			resetFlyTicks((EntityPlayerMP) e);
    		}
		}

		if (this.getSpeed() == Speed.FAST) {
			this.allWheels.forEach(wheel -> this.createWheelParticles(wheel, true));
		}
		this.allWheels.forEach(this::createWheelParticles);

		if (shouldStopUpdates()) {
			super.onEntityUpdate();
			return;
		}
		if (!world.isRemote) {
			if (prevWorldTime != -1) {
				world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(0.1f),
						this::canRunoverEntity).forEach(this::runOverEntity);
			}
		}
		if (this.isInWater()) {
			for (int i = 0; i < this.seats.length; i++) {
				Entity e = this.getEntityInSeat(i);
				if (e != null) {
					e.attackEntityFrom(DamageSource.DROWN, 0.5f);
				}
			}
			this.setHealth(this.getHealth() - 0.25F);
		}
		if (previousPosition == null) {
			previousPosition = this.getPositionVector();
		}
		estimatedSpeed = this.getPositionVector().distanceTo(previousPosition)
				/ (world.getTotalWorldTime() - prevWorldTime);
		previousPosition = this.getPositionVector();
		prevWorldTime = world.getTotalWorldTime();

		for (int i = 0; i < 4; i++) {
			List<WheelParticleData> markedRemoved = Lists.newArrayList();
			wheelDataList[i].forEach(wheel -> wheel.onUpdate(markedRemoved));
			markedRemoved.forEach(wheelDataList[i]::remove);
		}

		super.onEntityUpdate();

		this.allWheels.forEach(this::processWheel);

		Vector4d vec = wheeldata.carVector;
		this.backValue.setTarget(this.calculateWheelHeight(vec.y, false));
		this.frontValue.setTarget(this.calculateWheelHeight(vec.w, false));
		this.leftValue.setTarget(this.calculateWheelHeight(vec.z, true));
		this.rightValue.setTarget(this.calculateWheelHeight(vec.x, true));

		if (!this.world.isRemote) {
			this.updateHeal();
		}

		this.tickInterp();
		this.updateMotion();
		if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof EntityPlayer)) {
			this.setControlState((byte) 0);
		}
		if (this.world.isRemote) {
			this.handleControl();
		}
		this.applyMovement();
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
	}
	
	protected void updateHeal() {
		if (this.healCooldown > 0) {
			this.healCooldown--;
		} else if (this.healAmount > 0) {
			this.setHealth(this.getHealth() + 1);
			this.healAmount--;
			if (this.getHealth() > MAX_HEALTH) {
				this.setHealth(MAX_HEALTH);
				this.healAmount = 0;
			}
		}
	}

	protected boolean canRunoverEntity(Entity entity) {
		return EntitySelectors.NOT_SPECTATING.apply(entity) && !this.getPassengers().contains(entity);
	}

	protected void runOverEntity(Entity entity) {
		if (this.estimatedSpeed > 0.1D) {
			entity.attackEntityFrom(DamageSources.CAR, (float) (this.estimatedSpeed));
		}
	}

	protected void createWheelParticles(CarWheel wheel) {
		this.createWheelParticles(wheel, false);
	}

	protected void createWheelParticles(CarWheel wheel, boolean runBetween) {
		Vec3d pos;
		Vec3d opposite;
		if (runBetween) {
			Vec3d vec = wheel.getCurrentWheelPos();
			Vec3d oldVec = wheel.getCurrentWheelPos();

			Vec3d vec1 = wheel.getOppositeWheel().getCurrentWheelPos();
			Vec3d oldVec1 = wheel.getOppositeWheel().getCurrentWheelPos();

			pos = new Vec3d((vec.x + oldVec.x) / 2D, (vec.y + oldVec.y) / 2D, (vec.z + oldVec.z) / 2D);
			opposite = new Vec3d((vec1.x + oldVec1.x) / 2D, (vec1.y + oldVec1.y) / 2D, (vec1.z + oldVec1.z) / 2D);
		} else {
			pos = wheel.getCurrentWheelPos();
			opposite = wheel.getOppositeWheel().getCurrentWheelPos();
		}
		if (wheel.getCurrentWheelPos().distanceTo(wheel.getPrevCurrentWheelPos()) >= 0.05D) {
			this.wheelDataList[wheel.getID()].add(new WheelParticleData(pos, opposite, world.getTotalWorldTime())
					.setShouldRender(shouldTyresRender()));
		}
	}

	protected void processWheel(CarWheel wheel) {
		float localYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw);
		Vector2d relPos = wheel.getRelativeWheelPosition();
		double xRot = Math.sin(Math.toRadians(localYaw)) * relPos.y - Math.cos(Math.toRadians(localYaw)) * relPos.x;
		double zRot = -Math.cos(Math.toRadians(localYaw)) * relPos.y - Math.sin(Math.toRadians(localYaw)) * relPos.x;
		Vec3d vec = new Vec3d(posX + xRot, this.posY, posZ + zRot);
		wheel.setCurrentWheelPos(vec);

	}

	protected boolean shouldTyresRender() {
		return this.getSpeed() != Speed.SLOW;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(this.world.isRemote && !this.noiseInstance) {
			this.noiseInstance = true;
			this.startSound();
		}
	
		if (shouldStopUpdates()) {
			return;
		}
		if (JurassiCraftConfig.VEHICLES.destroyBlocks) {
			AxisAlignedBB aabb = this.getEntityBoundingBox();
			for (BlockPos pos : BlockPos.getAllInBoxMutable(
					new BlockPos(Math.floor(aabb.minX), Math.floor(aabb.minY), Math.floor(aabb.minZ)),
					new BlockPos(Math.ceil(aabb.maxX), Math.ceil(aabb.maxY), Math.ceil(aabb.maxZ)))) {
				IBlockState state = world.getBlockState(pos);
				if (state.getMaterial() == Material.VINE) {
					if (world.isRemote) {
						world.playEvent(2001, pos, Block.getStateId(state));
					} else {
						state.getBlock().dropBlockAsItem(world, pos, state, 0);
					}
					world.setBlockToAir(pos);
				} else if (state.getMaterial() == Material.LEAVES) {
					world.setBlockToAir(pos);
				}

			}
		}
		this.prevWheelRotateAmount = this.wheelRotateAmount;
		double deltaX = this.posX - this.prevPosX;
		double deltaZ = this.posZ - this.prevPosZ;
		float delta = Math.min(MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 4, 1);
		this.wheelRotateAmount += (delta - this.wheelRotateAmount) * 0.4F;
		this.wheelRotation += this.wheelRotateAmount;
		while (this.rotationYaw - this.prevRotationYaw < -180) {
			this.prevRotationYaw -= 360;
		}
		this.doBlockCollisions();
	}

	private void updateMotion() {
		final double resist = 0.8F;
		this.motionX *= resist;
		this.motionY *= resist;
		this.motionZ *= resist;
		this.rotationDelta *= resist;
		if (!this.hasNoGravity())
			this.motionY -= 0.15F;

	}

	@SideOnly(Side.CLIENT)
	protected void handleControl() {

		Entity driver = this.getControllingPassenger();
		if (!(driver instanceof EntityPlayer) || !((EntityPlayer) driver).isUser()) {
			return;
		}
		EntityPlayerSP player = (EntityPlayerSP) driver;
		MovementInput movementInput = player.movementInput;
		if (this.isInWater()) {
			this.setControlState((byte) 0);
		} else {

			this.left(movementInput.leftKeyDown);
			this.right(movementInput.rightKeyDown);
			this.forward(movementInput.forwardKeyDown);
			this.backward(movementInput.backKeyDown);
		}
		if (this.getControlState() != this.getPreviousState()) {
			JurassiCraft.NETWORK_WRAPPER.sendToServer(new UpdateVehicleControlMessage(this));
		}
		this.setPreviousState(this.getControlState());

	}

	protected void applyMovement() {
		Speed speed = this.getSpeed();
		float moveAmount = 0.0f;
		if ((this.left() || this.right()) && !(this.forward() || this.backward())) {
			moveAmount += 0.05F;
		}
		if (this.forward()) {
			moveAmount += 0.1F;
		} else if (this.backward()) {
			moveAmount -= 0.05F;
		}
		moveAmount *= (speed.modifier + this.speedModifier);
		if (this.isInWater()) {
			moveAmount -= 0.1f;
			if (moveAmount < 0f)
				moveAmount = 0f;
		}
		if (this.left()) {
			this.rotationDelta -= 20.0F * moveAmount;
		} else if (this.right()) {
			this.rotationDelta += 20.0F * moveAmount;
		}

		this.rotationDelta = MathHelper.clamp(this.rotationDelta, -30 * 0.1F, 30 * 0.1F);
		this.rotationYaw += this.rotationDelta;
		this.motionX += MathHelper.sin(-this.rotationYaw * 0.017453292F) * moveAmount;
		this.motionZ += MathHelper.cos(this.rotationYaw * 0.017453292F) * moveAmount;
	}

	private void tickInterp() {
		if (this.interpProgress > 0 && !this.canPassengerSteer()) {
			double interpolatedX = this.posX + (this.interpTargetX - this.posX) / (double) this.interpProgress;
			double interpolatedY = this.posY + (this.interpTargetY - this.posY) / (double) this.interpProgress;
			double interpolatedZ = this.posZ + (this.interpTargetZ - this.posZ) / (double) this.interpProgress;
			double deltaYaw = MathHelper.wrapDegrees(this.interpTargetYaw - (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + deltaYaw / (double) this.interpProgress);
			this.interpProgress--;
			this.setPosition(interpolatedX, interpolatedY, interpolatedZ);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}
	}

	protected final double calculateWheelHeight(double distance, boolean rotate90) {
		float localYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw);
		double ret = Integer.MIN_VALUE;

		Vector4d carVec = wheeldata.carVector;
		double sideLength = Math.abs(rotate90 ? carVec.x - carVec.z : carVec.z - carVec.w);
		for (double d = -sideLength; d <= sideLength; d += 0.25D/* TODO: config this ? */) {
			double xRot = Math.sin(Math.toRadians(localYaw)) * (rotate90 ? d : distance)
					- Math.cos(Math.toRadians(localYaw)) * (rotate90 ? distance : d);
			double zRot = -Math.cos(Math.toRadians(localYaw)) * (rotate90 ? d : distance)
					- Math.sin(Math.toRadians(localYaw)) * (rotate90 ? distance : d);
			Vec3d vec = new Vec3d(posX + xRot, this.posY, posZ + zRot);
			BlockPos pos = new BlockPos(vec);

			// world.spawnParticle(EnumParticleTypes.CRIT, vec.x, vec.y + 5, vec.z, 0, 0,
			// 0);

			boolean found = false;
			List<AxisAlignedBB> aabbList = Lists.newArrayList();
			while (!found) {
				if (pos.getY() < 0) {
					break;
				}
				aabbList.clear();
				world.getBlockState(pos).addCollisionBoxToList(world, pos, new AxisAlignedBB(pos), aabbList, this,
						false);
				if (world.isAirBlock(pos) || aabbList.isEmpty()) {
					pos = pos.down();
				} else {
					found = true;
				}
			}
			if (!found) {
				ret = posY;
			}
			if (found && !world.isAirBlock(pos.up()) && !world.isAirBlock(pos.up(2))) {
				List<AxisAlignedBB> list = Lists.newArrayList();
				world.getBlockState(pos.up()).addCollisionBoxToList(world, pos.up(), new AxisAlignedBB(pos.up()), list,
						this, false);
				world.getBlockState(pos.up(2)).addCollisionBoxToList(world, pos.up(2), new AxisAlignedBB(pos.up(2)),
						list, this, false);
				if (!list.isEmpty()) {
					ret = posY;
				}
			}
			if (aabbList.isEmpty()) {
				ret = pos.getY() + 1;
			}
			DoubleHolder holder = new DoubleHolder(Integer.MIN_VALUE);
			aabbList.forEach(aabb -> holder.value = Math.max(aabb.maxY, holder.value));
			if (holder.value > ret) {
				ret = holder.value;
			}
		}
		return ret;
	}

	// TODO: Fix interaction for 1.21 - method signature changed
	public boolean processInitialInteract(Player player, InteractionHand hand) {
		if (!level().isClientSide) {
			if (player.getVehicle() == this) {
				ItemStack currentStack = this.entityData.get(RECORD_ITEM);
				ItemStack stack = player.getItemInHand(hand);
				// TODO: Update ItemRecord check for 1.21
				if (stack.isEmpty()) {
					this.entityData.set(RECORD_ITEM, stack);
					player.setItemInHand(hand, currentStack);
					// TODO: Fix network packet for 1.21
				}
			} else if (!player.isShiftKeyDown()) {
				player.startRiding(this);
			}
		}
		return true;
	}

	@Override
	protected void addPassenger(Entity passenger) {
		super.addPassenger(passenger);
		if (passenger instanceof EntityPlayerMP) {
			resetFlyTicks((EntityPlayerMP) passenger);
		}
		if (!passenger.world.isRemote)
			this.usherPassenger(passenger, 0);
	}

	private void usherPassenger(Entity passenger, int start) {
		for (int i = start; i < this.seats.length; i++) {
			if (this.tryPutInSeat(passenger, i, false)) {
				return;
			}
		}
	}

	private void setSeat(String seatID, String uuid) {
		NBTTagCompound comp = new NBTTagCompound();
		comp.merge(this.dataManager.get(WATCHER_SEATS));
		comp.setString(seatID, uuid);
		this.dataManager.set(WATCHER_SEATS, comp);
		if (seatID.equals("0")) {
			if (this.getControllingPassenger() == null) {
				if (this.getControlState() != (byte) 0) {
					this.setControlState((byte) 0);
					if(this.world.isRemote)
						JurassiCraft.NETWORK_WRAPPER.sendToServer(new UpdateVehicleControlMessage(this));//asd
				}
			}
		}
	}

	@Override
	public boolean tryPutInSeat(Entity passenger, int seatID, boolean isPacket) {
		if (seatID < this.seats.length && seatID >= 0) {
			int seatNumber = getSeatForEntity(passenger);
			if ((seatNumber == 0 && seatID == 0 && !isPacket) || getIfExists(seatID, false).equals("")) {
				if (seatNumber != -1) {

					if (!passenger.world.isRemote) {
						setSeat(str(seatNumber), "");
					}
				}
				if (!passenger.world.isRemote)
					setSeat(str(seatID), str(passenger.getEntityId()));

				return true;
			}
		}
		return false;
	}

	@Nonnull
	private String str(int input) {

		return Integer.toString(input);
	}

	@Nullable
	@Override
	public int getSeatForEntity(Entity entity) {
		for (int i = 0; i < this.seats.length; i++) {
			if (getIfExists(i, true).equals(str(entity.getEntityId()))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	protected void removePassenger(Entity passenger) {

		super.removePassenger(passenger);

	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		}
		if (!this.world.isRemote) {
			if (source.getTrueSource() instanceof EntityPlayer) {
				amount *= 10;
				this.healAmount += amount;
				this.healCooldown = 40;
			}
			this.setHealth(this.getHealth() - amount);
		}
		return true;
	}

	@Override
	public void updatePassenger(Entity passenger) {
		if (this.isPassenger(passenger)) {
			Seat seat = null;
			if (getSeatForEntity(passenger) != -1)
				seat = this.seats[getSeatForEntity(passenger)];
			Vec3d pos;
			if (seat == null) {

				pos = new Vec3d(this.posX, this.posY + this.height, this.posZ);
			} else {
				pos = seat.getPos();
			}
			passenger.setPosition(pos.x, pos.y, pos.z);
			passenger.rotationYaw += this.rotationDelta;
			passenger.setRotationYawHead(passenger.getRotationYawHead() + this.rotationDelta);
			if (passenger instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) passenger;
				living.renderYawOffset += (living.rotationYaw - living.renderYawOffset) * 0.6F;
			}
		}
	}

	public Seat getSeat(int id) {
		if (id < seats.length) {
			return seats[id];
		}
		return null;
	}

	protected boolean shouldStopUpdates() {
		return false;
	}

	@Override
	public void setDead() {
		JurassiCraft.getLogger().debug("The vehicle at the coordinates " + this.getPosition() + " has been destroyed! " + this.getHealth() + " "+ this.motionX + " " + this.motionY + " " + this.motionZ);
		super.setDead();
		TyretrackRenderer.uploadList(this);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.setHealth(compound.getFloat("Health"));
		this.healAmount = compound.getFloat("HealAmount");
		this.setSpeed(Speed.values()[compound.getInt("Speed")]);
		CompoundTag tag = compound.getCompound("InterpValues");

		this.backValue.deserializeNBT(tag.getCompound("Back"));
		this.frontValue.deserializeNBT(tag.getCompound("Front"));
		this.leftValue.deserializeNBT(tag.getCompound("Left"));
		this.rightValue.deserializeNBT(tag.getCompound("Right"));
		this.entityData.set(RECORD_ITEM, ItemStack.of(compound.getCompound("RecordItem")));

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putFloat("Health", this.getHealth());
		compound.putFloat("HealAmount", this.healAmount);
		compound.putInt("Speed", this.getSpeed().ordinal());
		CompoundTag tag = new CompoundTag();
		tag.put("Back", this.backValue.serializeNBT());
		tag.put("Front", this.frontValue.serializeNBT());
		tag.put("Left", this.leftValue.serializeNBT());
		tag.put("Right", this.rightValue.serializeNBT());
		compound.put("InterpValues", tag);
		compound.put("RecordItem", this.entityData.get(RECORD_ITEM).save(new CompoundTag()));
	}

	public void startSound() {

		ClientProxy.playCarSound(this);
	}

	public final class Seat {

		private final InterpValue interpValue = new InterpValue(VehicleEntity.this, 0.1D);

		private float offsetX;
		private float offsetY;
		private float offsetZ;

		private final float radius;
		private final float height;

		public Seat(float offsetX, float offsetY, float offsetZ, float radius, float height) {
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
			this.radius = radius;
			this.height = height;
		}

		protected void so(float x, float y, float z) {
			this.offsetX = x;
			this.offsetY = y;
			this.offsetZ = z;
		}

		public Vec3 getPos() {
			double theta = Math.toRadians(VehicleEntity.this.getYRot());
			double sideX = Math.cos(theta);
			double sideZ = Math.sin(theta);
			double forwardTheta = theta + Math.PI / 2;
			double forwardX = Math.cos(forwardTheta);
			double forwardZ = Math.sin(forwardTheta);
			double x = VehicleEntity.this.getX() + sideX * this.offsetX + forwardX * this.offsetZ;
			double y = VehicleEntity.this.getY() + this.offsetY;
			double z = VehicleEntity.this.getZ() + sideZ * this.offsetX + forwardZ * this.offsetZ;
			return new Vec3(x, y, z);
		}
		
		public float getOffsetY() {
			return this.offsetY;
		}
		
		public float getOffsetZ() {
			return this.offsetZ;
		}

		public AABB getBounds() {
			Vec3 pos = this.getPos();
			double x = pos.x;
			double y = pos.y;
			double z = pos.z;
			return new AABB(x - this.radius, y, z - this.radius, x + this.radius,
					y + this.offsetY + this.height, z + this.radius);
		}

		public InterpValue getInterpValue() {
			return interpValue;
		}
	}

	protected final class WheelData {
		// TODO: Replace Vector2d/Vector4d with appropriate alternatives
		public final Object bl;
		public final Object br;
		public final Object fl;
		public final Object fr;

		public final Object carVector;

		public WheelData(double backLeftX, double backLeftZ, double frontRightX, double frontRightZ) {
			bl = null; // new Vector2d(backLeftX, backLeftZ);
			br = null; // new Vector2d(frontRightX, backLeftZ);
			fl = null; // new Vector2d(backLeftX, frontRightZ);
			fr = null; // new Vector2d(frontRightX, frontRightZ);

			carVector = null; // new Vector4d(backLeftX, backLeftZ, frontRightX, frontRightZ);
		}
	}

	public enum Speed {

		// The modifiers ARE hardcoded. If you want to change them, please talk to me
		// first. The tyre mark code relies on the modifiers being how they are
		SLOW(0.5f), MEDIUM(1f), FAST(2f);

		public final float modifier;

		Speed(float modifier) {
			this.modifier = modifier;
		}
	}

	protected abstract Seat[] createSeats();

	protected abstract WheelData createWheels();

	public abstract void dropItems();

	public float getSoundVolume() {
		return (Math.abs(this.wheelRotateAmount) + 0.001F)
				/ (this.sound == null || this.sound.isDonePlaying() ? 2f : 4f);
	}

	public void doPlayerRotations(Player player, float partialTicks) {
//		float rotation = (float) Math.toRadians(player.rotationYaw - this.rotationYaw);
//		float offsetY = seats[getSeatForEntity(player)].getOffsetY();
//		float offsetZ = seats[getSeatForEntity(player)].getOffsetZ();
//		GlStateManager.translate(Math.sin(-rotation) * offsetZ, offsetY + tmpY, Math.cos(rotation) * offsetZ);
//		GlStateManager.rotate((float) ((Math.sin(rotation) * this.pitch) + (Math.cos(rotation) * this.roll)), 0, 0, 1);
//		GlStateManager.rotate((float) ((Math.cos(rotation) * this.pitch) + (Math.sin(-rotation) * this.roll)), 1, 0, 0);
//		GlStateManager.translate(-Math.sin(-rotation) * offsetZ, -offsetY - tmpY, -Math.cos(rotation) * offsetZ );
//		System.out.println(this.pitch + " : " + this.rotationYaw);
	}
}
