package org.jurassicraft.server.entity.ai.metabolism;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.ai.Mutex;
import org.jurassicraft.server.food.FoodHelper;
import org.jurassicraft.server.util.GameRuleHandler;

import java.util.List;

public class EatFoodItemEntityAI extends EntityAIBase {
    protected DinosaurEntity dinosaur;

    protected EntityItem item;

    public EatFoodItemEntityAI(DinosaurEntity dinosaur) {
        this.dinosaur = dinosaur;
        this.setMutexBits(Mutex.METABOLISM);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.dinosaur.isCarcass() && GameRuleHandler.DINO_METABOLISM.getBoolean(this.dinosaur.world)) {
            if (this.dinosaur.getMetabolism().isHungry()) {
                double closestDistance = Integer.MAX_VALUE;
                EntityItem closest = null;
                boolean found = false;
                World world = this.dinosaur.world;
                List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, this.dinosaur.getEntityBoundingBox().expand(16, 16, 16));
                for (EntityItem entity : items) {
                    ItemStack stack = entity.getItem();
                    Item item = stack.getItem();
                    if (FoodHelper.isEdible(this.dinosaur, this.dinosaur.getDinosaur().getMetadata().getDiet(), item)) {
                        double distance = this.dinosaur.getDistanceSq(entity);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closest = entity;
                            found = true;
                        }
                    }
                }
                if (found) {
                    this.dinosaur.getNavigator().tryMoveToEntityLiving(closest, 1.0);
                    this.item = closest;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.dinosaur != null && !this.dinosaur.getNavigator().noPath() && this.item != null && !this.item.isRemoved();
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }
}
