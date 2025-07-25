package org.jurassicraft.server.block;

import net.minecraft.world.level.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.server.tab.TabHandler;

public class ReinforcedGlassBlock extends BlockGlass {
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");

	public ReinforcedGlassBlock() {
		super(Material.GLASS, false);
		this.setCreativeTab(TabHandler.BLOCKS);
		setHardness(10.0F);
		setResistance(14.0F);
		this.setSoundType(SoundType.STONE);
		this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false)
				.withProperty(SOUTH, false).withProperty(WEST, false).withProperty(UP, false)
				.withProperty(DOWN, false));
	}

	public boolean canConnectTo(IBlockAccess world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() instanceof ReinforcedGlassBlock;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.withProperty(NORTH, this.canConnectTo(world, pos.north()))
				.withProperty(EAST, this.canConnectTo(world, pos.east()))
				.withProperty(SOUTH, this.canConnectTo(world, pos.south()))
				.withProperty(WEST, this.canConnectTo(world, pos.west()))
				.withProperty(UP, this.canConnectTo(world, pos.up()))
				.withProperty(DOWN, this.canConnectTo(world, pos.down()));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, NORTH, EAST, WEST, SOUTH, UP, DOWN);
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return world.getBlockState(pos.offset(side.getOpposite())) != world.getBlockState(pos)
				&& super.shouldSideBeRendered(state, world, pos, side);
	}

}
