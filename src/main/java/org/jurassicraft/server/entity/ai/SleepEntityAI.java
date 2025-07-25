package org.jurassicraft.server.entity.ai;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.entity.DinosaurEntity;
import org.jurassicraft.server.entity.ai.util.AIUtils;

public class SleepEntityAI extends EntityAIBase {
    protected DinosaurEntity dinosaur;

    protected Path path;
    protected int giveUpTime;

    public SleepEntityAI(DinosaurEntity dinosaur) {
        this.dinosaur = dinosaur;
        this.setMutexBits(Mutex.METABOLISM | Mutex.MOVEMENT | Mutex.ANIMATION);
    }

    @Override
    public boolean shouldExecute() {
        World world = this.dinosaur.world;

        boolean marineAnimal = this.dinosaur.getDinosaur().getMetadata().isMarineCreature();

        if ((this.dinosaur.onGround || this.dinosaur.isRiding() || marineAnimal) && this.dinosaur.isAlive() && this.dinosaur.shouldSleep() && !this.dinosaur.isSleeping() && this.dinosaur.getStayAwakeTime() <= 0) {
            if (marineAnimal) {
                return true;
            }

            int range = 8;

            int posX = (int) this.dinosaur.getX();
            int posZ = (int) this.dinosaur.getZ();

            for (int x = posX - range; x < posX + range; x++) {
                for (int z = posZ - range; z < posZ + range; z++) {
                    BlockPos possiblePos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));

                    if (world.isAirBlock(possiblePos) && world.getBlockState(possiblePos.down()).getMaterial() != Material.WATER) {
                        if (this.canFit(possiblePos) && !world.canSeeSky(possiblePos) && this.dinosaur.setSleepLocation(possiblePos, true)) {
                            this.path = this.dinosaur.getNavigator().getPath();
                            break;
                        }
                    }
                }
            }

            if (this.dinosaur.inWater()) {
                BlockPos shore = AIUtils.findShore(this.dinosaur.world, this.dinosaur.getPosition());
                if (shore != null) {
                    if (this.dinosaur.getNavigator().tryMoveToXYZ(shore.getX(), shore.getY(), shore.getZ(), 1.0)) {
                        this.path = this.dinosaur.getNavigator().getPath();
                    }
                }
            }

            return true;
        }

        return false;
    }

    private boolean canFit(BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        AxisAlignedBB boundingBox = new AxisAlignedBB(x, y, z, x + this.dinosaur.width, y + this.dinosaur.height, z + this.dinosaur.width);

        return this.dinosaur.world.getCollisionBoxes(this.dinosaur, boundingBox).isEmpty() && this.dinosaur.world.getEntitiesWithinAABBExcludingEntity(this.dinosaur, boundingBox).isEmpty();
    }

    @Override
    public void startExecuting() {
        this.giveUpTime = 400;
    }

    @Override
    public void updateTask() {
        Path currentPath = this.dinosaur.getNavigator().getPath();

        if (this.path != null) {
            PathPoint finalPathPoint = this.path.getFinalPathPoint();

            if (currentPath == null || !currentPath.getFinalPathPoint().equals(finalPathPoint)) {
                Path path = this.dinosaur.getNavigator().getPathToXYZ(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z);
                this.dinosaur.getNavigator().setPath(path, 1.0);
                this.path = path;
            }
        }

        if (this.giveUpTime <= 0 || (this.dinosaur.getStayAwakeTime() <= 0 && (this.path == null || this.path.isFinished()))) {
            this.dinosaur.setSleeping(true);
        }

        this.giveUpTime--;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.dinosaur != null && !this.dinosaur.isCarcass() && !this.dinosaur.isSleeping() && this.dinosaur.shouldSleep();
    }

    @Override
    public void resetTask() {
        this.dinosaur.setSleeping(true);
    }
}
