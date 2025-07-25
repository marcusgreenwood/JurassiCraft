package org.jurassicraft.server.block.tree;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

import java.util.Locale;
import java.util.Random;

public class AncientDoubleSlabBlock extends AncientSlabBlock {
    private Block singleSlab;

    public AncientDoubleSlabBlock(TreeType treeType, Block slab, IBlockState state) {
        super(treeType, state);
        this.setUnlocalizedName(treeType.name().toLowerCase(Locale.ENGLISH) + "_double_slab");
        this.singleSlab = slab;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this.singleSlab);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this.singleSlab));
    }

    @Override
    public boolean isDouble() {
        return true;
    }
}
