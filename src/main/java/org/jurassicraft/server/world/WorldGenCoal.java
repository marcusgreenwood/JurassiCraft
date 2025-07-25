package org.jurassicraft.server.world;

import com.google.common.base.Predicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenMinable;
import org.jurassicraft.server.block.BlockHandler;

import java.util.Random;

public class WorldGenCoal extends WorldGenMinable {
    private final IBlockState oreBlock;
    private final int veinSize;
    private final Predicate<IBlockState> predicate;

    public WorldGenCoal(IBlockState state, int blockCount) {
        this(state, blockCount, BlockMatcher.forBlock(Blocks.STONE));
    }

    public WorldGenCoal(IBlockState state, int blockCount, Predicate<IBlockState> predicate) {
        super(state, blockCount, predicate);
        this.oreBlock = state;
        this.veinSize = blockCount;
        this.predicate = predicate;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos position) {
        float f = rand.nextFloat() * (float) Math.PI;
        double d0 = (double) ((float) (position.getX() + 8) + MathHelper.sin(f) * (float) this.veinSize / 8.0F);
        double d1 = (double) ((float) (position.getX() + 8) - MathHelper.sin(f) * (float) this.veinSize / 8.0F);
        double d2 = (double) ((float) (position.getZ() + 8) + MathHelper.cos(f) * (float) this.veinSize / 8.0F);
        double d3 = (double) ((float) (position.getZ() + 8) - MathHelper.cos(f) * (float) this.veinSize / 8.0F);
        double d4 = (double) (position.getY() + rand.nextInt(3) - 2);
        double d5 = (double) (position.getY() + rand.nextInt(3) - 2);

        for (int i = 0; i < this.veinSize; ++i) {
            float f1 = (float) i / (float) this.veinSize;
            double d6 = d0 + (d1 - d0) * (double) f1;
            double d7 = d4 + (d5 - d4) * (double) f1;
            double d8 = d2 + (d3 - d2) * (double) f1;
            double d9 = rand.nextDouble() * (double) this.veinSize / 16.0D;
            double d10 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            int j = MathHelper.floor(d6 - d10 / 2.0D);
            int k = MathHelper.floor(d7 - d11 / 2.0D);
            int l = MathHelper.floor(d8 - d10 / 2.0D);
            int i1 = MathHelper.floor(d6 + d10 / 2.0D);
            int j1 = MathHelper.floor(d7 + d11 / 2.0D);
            int k1 = MathHelper.floor(d8 + d10 / 2.0D);

            for (int l1 = j; l1 <= i1; ++l1) {
                double d12 = ((double) l1 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        double d13 = ((double) i2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int j2 = l; j2 <= k1; ++j2) {
                                double d14 = ((double) j2 + 0.5D - d8) / (d10 / 2.0D);

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    BlockPos pos = new BlockPos(l1, i2, j2);

                                    IBlockState previousState = world.getBlockState(pos);

                                    if (previousState.getBlock().isReplaceableOreGen(previousState, world, pos, this.predicate)) {
                                        if (rand.nextInt(10) == 0) {
                                            world.setBlockState(pos, BlockHandler.PLANT_FOSSIL.getDefaultState());
                                        } else {
                                            world.setBlockState(pos, this.oreBlock, 2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}