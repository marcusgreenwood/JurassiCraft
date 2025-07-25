package org.jurassicraft.server.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import org.jurassicraft.server.tab.TabHandler;

public class SaplingSeedItem extends Item implements IPlantable {
    private Block sapling;

    public SaplingSeedItem(Block sapling) {
        this.sapling = sapling;
        this.setCreativeTab(TabHandler.PLANTS);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        if (facing == Direction.UP && player.canPlayerEdit(pos.offset(facing), facing, stack) && state.getBlock().canSustainPlant(state, world, pos, Direction.UP, this) && world.isAirBlock(pos.up())) {
            world.setBlockState(pos.up(), this.sapling.getDefaultState());
            stack.shrink(1);
            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Plains;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return this.sapling.getDefaultState();
    }
}