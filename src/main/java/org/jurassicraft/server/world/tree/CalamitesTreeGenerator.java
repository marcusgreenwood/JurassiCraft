package org.jurassicraft.server.world.tree;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockLog;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import org.jurassicraft.server.block.BlockHandler;
import org.jurassicraft.server.block.tree.AncientLeavesBlock;
import org.jurassicraft.server.block.tree.AncientLogBlock;
import org.jurassicraft.server.block.tree.AncientSaplingBlock;
import org.jurassicraft.server.block.tree.TreeType;

import java.util.Random;

public class CalamitesTreeGenerator extends WorldGenAbstractTree {
    public CalamitesTreeGenerator() {
        super(true);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
        IBlockState log = BlockHandler.ANCIENT_LOGS.get(TreeType.CALAMITES).getDefaultState();
        IBlockState leaves = BlockHandler.ANCIENT_LEAVES.get(TreeType.CALAMITES).getDefaultState();

        int height = rand.nextInt(10) + 10;
        int branchIndex = 0;

        int halfDistance = height / 2;

        for (int y = 0; y < height; y++) {
            BlockPos logPos = position.up(y);
            this.setBlockState(world, logPos, log);

            boolean upperHalf = y > halfDistance;

            branchIndex++;

            if (branchIndex > (upperHalf ? 2 : 3)) {
                branchIndex = 0;
            }

            boolean branch = upperHalf ? branchIndex >= 2 : branchIndex >= 3;

            if (branch) {
                for (int face = 0; face < 4; face++) {
                    EnumFacing facing = EnumFacing.getHorizontal(face);
                    BlockPos branchPos = logPos.offset(facing);
                    IBlockState facingLog = log.withProperty(AncientLogBlock.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()));

                    this.setBlockState(world, branchPos, facingLog);
                    this.setBlockState(world, branchPos.up(), leaves);

                    int leaveOut = Math.max(1, (upperHalf ? -(halfDistance - y) : (halfDistance - y) + halfDistance) / 2) + (rand.nextInt(2) - 1);

                    for (int i = 0; i < leaveOut; i++) {
                        BlockPos leavePos = branchPos.offset(facing, i + 1).up(i / 2 + 1);

                        this.setBlockState(world, leavePos, leaves);

                        if (!upperHalf) {
                            if (i < leaveOut / 4 || height < 12) {
                                this.setBlockState(world, leavePos.up(), leaves);
                            }

                            if (i < leaveOut - 2) {
                                this.setBlockState(world, leavePos.down(), leaves);
                                this.setBlockState(world, leavePos.offset(facing.rotateYCCW()), leaves);
                                this.setBlockState(world, leavePos.offset(facing.rotateY()), leaves);
                            } else if (i >= leaveOut - 2) {
                                this.setBlockState(world, leavePos.up(), leaves);
                            }
                        } else if (i >= leaveOut - 1) {
                            this.setBlockState(world, leavePos.up(), leaves);
                            this.setBlockState(world, leavePos.up(1).offset(facing), leaves);
                        }
                    }

                    if (!upperHalf) {
                        this.setBlockState(world, branchPos.offset(facing).up(), facingLog);
                        this.setBlockState(world, branchPos.offset(facing), leaves);
                    } else {
                        this.setBlockState(world, branchPos.offset(facing).up(2), leaves);
                    }
                }
            }
        }

        for (int i = 0; i < height / 4 + 1; i++) {
            this.setBlockState(world, position.up(height + i), leaves);
        }

        return true;
    }

    private void setBlockState(World world, BlockPos pos, IBlockState state) {
        Block block = world.getBlockState(pos).getBlock();
        if (this.canGrowInto(block) || block instanceof AncientLeavesBlock || block instanceof AncientSaplingBlock || block instanceof AncientLogBlock) {
            world.setBlockState(pos, state);
        }
    }
}
