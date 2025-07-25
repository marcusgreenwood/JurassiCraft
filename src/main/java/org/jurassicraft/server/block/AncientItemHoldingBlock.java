package org.jurassicraft.server.block;

import java.util.List;
import java.util.Random;

import org.jurassicraft.server.block.entity.AncientItemHoldingBlockEntity;
import org.jurassicraft.server.tab.TabHandler;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockSand;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

public class AncientItemHoldingBlock extends Block implements ITileEntityProvider {
	
	public AncientItemHoldingBlock() {
		
		super(Material.SAND);
		setUnlocalizedName("ancient_item_holding_block");
		setSoundType(SoundType.SAND);
		setHardness(0.5F);
		this.hasTileEntity = true;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		super.onBlockHarvested(worldIn, pos, state, player);
		AncientItemHoldingBlockEntity tile = (AncientItemHoldingBlockEntity) worldIn.getTileEntity(pos);
		if (tile != null && tile.getDisplayItemStack() != null) {
			spawnAsEntity(worldIn, pos, tile.getDisplayItemStack());
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(Blocks.SAND);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AncientItemHoldingBlockEntity();
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Blocks.SAND);
	}

}
