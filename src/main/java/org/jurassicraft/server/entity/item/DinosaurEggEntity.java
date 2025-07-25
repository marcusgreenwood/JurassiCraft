package org.jurassicraft.server.entity.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityList;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.item.ItemHandler;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

public class DinosaurEggEntity extends Entity implements IEntityAdditionalSpawnData {
    private DinosaurEntity entity;
    private UUID parent;
    private Dinosaur dinosaur;
    private int hatchTime;

    public DinosaurEggEntity(World world, DinosaurEntity entity, DinosaurEntity parent) {
        this(world);
        this.entity = entity;
        this.dinosaur = entity.getDinosaur();
        this.parent = parent.getUniqueID();
    }

    public DinosaurEggEntity(World world) {
        super(world);
        this.setSize(0.3F, 0.5F);
        this.hatchTime = this.random(5000, 6000);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(dinosaur == null) {
            Optional<Entity> parentEntity =  world.loadedEntityList.stream().filter(entity -> entity.getUniqueID().equals(this.parent)).findFirst();
            if(parentEntity.isPresent() && parentEntity.get() instanceof DinosaurEntity) {
                this.dinosaur = ((DinosaurEntity)parentEntity.get()).getDinosaur();
            }
        }

        if (!this.level().isClientSide) {
            if (this.entity == null) {
                this.discard();
            }

            this.hatchTime--;

            if (this.hatchTime <= 0) {
                this.hatch();
            }

            if (!this.onGround) {
                this.motionY -= 0.035D;
            }

            this.motionX *= 0.85;
            this.motionY *= 0.85;
            this.motionZ *= 0.85;

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        }
    }

    @Override
    public void entityInit() {
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (this.entity != null && !this.level().isClientSide) {
            ItemStack eggStack = new ItemStack(ItemHandler.EGG, 1, EntityHandler.getDinosaurId(this.entity.getDinosaur()));
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("DNAQuality", this.entity.getDNAQuality());
            nbt.setString("Genetics", this.entity.getGenetics());
            eggStack.setTagCompound(nbt);
            this.entityDropItem(eggStack, 0.1F);
            this.discard();
        }

        return true;
    }

    public void hatch() {
        if(dinosaur != null) {
            try {
            	DinosaurEntity entity = this.dinosaur.construct(this.world);
                entity.setPosition(this.getX(), this.getY(), this.getZ());
                entity.setAge(0);
                this.world.spawnEntity(entity);
                entity.playLivingSound();
                this.discard();
                for (Entity loadedEntity : this.world.loadedEntityList) {
                    if (loadedEntity instanceof DinosaurEntity && loadedEntity.getUniqueID().equals(this.parent)) {
                        DinosaurEntity parent = (DinosaurEntity) loadedEntity;
                        if (parent.family != null && this.dinosaur.getMetadata().shouldDefendOffspring()) {
                            parent.family.addChild(entity.getUniqueID());
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int random(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.hatchTime = compound.getInteger("HatchTime");
        NBTTagCompound entityTag = compound.getCompoundTag("Hatchling");
        this.entity = (DinosaurEntity) EntityList.createEntityFromNBT(entityTag, this.world);
        this.parent = compound.getUniqueId("Parent");
        this.dinosaur = EntityHandler.getDinosaurById(compound.getInteger("DinosaurID"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("HatchTime", this.hatchTime);
        if (this.entity != null) {
            compound.setTag("Hatchling", this.entity.serializeNBT());
        }
        compound.setUniqueId("Parent", this.parent);
        compound.setInteger("DinosaurID", EntityHandler.getDinosaurId(dinosaur));
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(EntityHandler.getDinosaurId(this.entity != null ? this.entity.getDinosaur() : EntityHandler.getDinosaurById(0)));
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        this.dinosaur = EntityHandler.getDinosaurById(additionalData.readInt());
    }

    @Nullable
    public Dinosaur getDinosaur() {
        return this.dinosaur;
    }
}