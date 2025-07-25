package org.jurassicraft.server.block.machine;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.init.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.level.Level;

import java.util.Random;

import org.jurassicraft.server.block.BlockHandler;

public class CultivatorTopBlock extends CultivatorBlock {
    public CultivatorTopBlock() {
        super("top");
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult result, World world, BlockPos pos, EntityPlayer player) {
        Item item = Item.getItemFromBlock(BlockHandler.CULTIVATOR_BOTTOM);

        if (item == null) {
            return null;
        }

        Block block = item instanceof ItemBlock ? getBlockFromItem(item) : this;
        return new ItemStack(item, 1, block.getMetaFromState(world.getBlockState(pos)));
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    	return Item.getItemFromBlock(BlockHandler.CULTIVATOR_BOTTOM);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        BlockPos add = pos.add(0, -1, 0);
        IBlockState blockState = world.getBlockState(add);

        return blockState.getBlock().onBlockActivated(world, add, blockState, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        BlockPos bottomBlock = pos.add(0, -1, 0);

        if (world.getBlockState(bottomBlock).getBlock() != BlockHandler.CULTIVATOR_BOTTOM) {
            world.setBlockState(bottomBlock, BlockHandler.CULTIVATOR_BOTTOM.getDefaultState().withProperty(COLOR, state.getValue(COLOR)));
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos.add(0, -1, 0), Blocks.AIR.getDefaultState());
        super.breakBlock(world, pos, state);
    }
}
