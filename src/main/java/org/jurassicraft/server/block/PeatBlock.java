package org.jurassicraft.server.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockDirt;
import net.minecraft.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.biome.BiomeSwamp;

import org.apache.commons.lang3.tuple.Pair;
import org.jurassicraft.server.conf.JurassiCraftConfig;
import org.jurassicraft.server.plugin.jei.category.moss.PeatItem;
import org.jurassicraft.server.tab.TabHandler;

import java.util.List;
import java.util.Random;

public class PeatBlock extends Block implements PeatItem {
    public static final PropertyInteger MOISTURE = PropertyInteger.create("moisture", 0, 7);
    public int peatGenerationSpeed = JurassiCraftConfig.PLANT_GENERATION.peatGenerationSpeed;

    public PeatBlock() {
        super(Material.GROUND);
        this.setCreativeTab(TabHandler.PLANTS);
        this.setSoundType(SoundType.GROUND);
        this.setHardness(0.5F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, 0));
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        int moisture = state.getValue(MOISTURE);

        if (!this.hasWater(world, pos) && !world.isRainingAt(pos.up()) && !(world.getBiome(pos) instanceof BiomeSwamp)) {
            if (moisture > 0) {
                world.setBlockState(pos, state.withProperty(MOISTURE, moisture - 1), 2);
            } else {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            }
        } else if (moisture < 7) {
            world.setBlockState(pos, state.withProperty(MOISTURE, 7), 2);
        }
        if (world.getBiome(pos) instanceof BiomeSwamp) {
            for (int i = 0; i < 4; ++i) {
                BlockPos blockpos = pos.add(rand.nextInt(peatGenerationSpeed + 1), rand.nextInt(peatGenerationSpeed + 1), rand.nextInt(peatGenerationSpeed + 1));

                if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !world.isBlockLoaded(blockpos)) {
                    return;
                }

                IBlockState iblockstate = world.getBlockState(blockpos.up());
                IBlockState iblockstate1 = world.getBlockState(blockpos);
                IBlockState iBlockState2 = world.getBlockState(blockpos.up(2));
                IBlockState iBlockState3 = world.getBlockState(blockpos.up(3));


                if (iblockstate1.getBlock() == Blocks.DIRT && iblockstate1.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT) {
                    if (iblockstate.getBlock() == Blocks.GRASS || iBlockState2.getBlock() == Blocks.GRASS || iBlockState3.getBlock() == Blocks.GRASS) {
                        world.setBlockState(blockpos, BlockHandler.PEAT.getDefaultState());
                    }
                }
            }
        }
    }

    private boolean hasWater(World world, BlockPos pos) {
        for (BlockPos.MutableBlockPos nearPos : BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (world.getBlockState(nearPos).getMaterial() == Material.WATER) {
                return true;
            }
        }

        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(MOISTURE, meta & 7);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(MOISTURE);
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MOISTURE);
    }

	@Override
	public List<Pair<Float, ItemStack>> getChancedOutputs(ItemStack inputItem) {
		return null;
	}
}
