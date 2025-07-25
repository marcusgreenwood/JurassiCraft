package org.jurassicraft.server.block.plant;

import java.util.Random;

import org.jurassicraft.server.item.ItemHandler;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.level.Level;

/**
 * Created by Codyr on 30/10/2016.
 */
public class EnallheliaBlock extends AncientCoralBlock {
	
        private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.1F, 0.0F, 0.1F, 0.9F, 0.8F, 0.9F);

        @Override
        public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
            return BOUNDS;
        }
        
        @Override
    	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
    		return ItemHandler.ENALLHELIA;
    	}
        
        @Override
    	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {

    		return new ItemStack(ItemHandler.ENALLHELIA);
    	}


    }

