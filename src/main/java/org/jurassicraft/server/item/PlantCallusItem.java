package org.jurassicraft.server.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockCrops;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.init.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.block.plant.DoublePlantBlock;
import org.jurassicraft.server.block.plant.JCBlockCropsBase;
import org.jurassicraft.server.plant.Plant;
import org.jurassicraft.server.plant.PlantHandler;
import org.jurassicraft.server.tab.TabHandler;
import org.jurassicraft.server.util.LangUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PlantCallusItem extends Item {
    public PlantCallusItem() {
        super();
        this.setCreativeTab(TabHandler.PLANTS);
        this.setHasSubtypes(true);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return LangUtils.translate(this.getUnlocalizedName() + ".name").replace("{plant}", LangUtils.getPlantName(stack.getItemDamage()));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	ItemStack stack = player.getHeldItem(hand);
        if (side == Direction.UP && player.canPlayerEdit(pos.offset(side), side, stack)) {
            if (world.isAirBlock(pos.offset(side)) && world.getBlockState(pos).getBlock() == Blocks.FARMLAND) {
                Plant plant = PlantHandler.getPlantById(stack.getItemDamage());

                if (plant != null) {
                    Block block = plant.getBlock();

                    if (!(block instanceof BlockCrops || block instanceof JCBlockCropsBase)) {
                        world.setBlockState(pos, Blocks.DIRT.getDefaultState());
                    }

                    if (block instanceof DoublePlantBlock) {
                        world.setBlockState(pos.up(), block.getDefaultState().withProperty(DoublePlantBlock.HALF, DoublePlantBlock.BlockHalf.LOWER));
                        world.setBlockState(pos.up(2), block.getDefaultState().withProperty(DoublePlantBlock.HALF, DoublePlantBlock.BlockHalf.UPPER));
                    } else {
                        world.setBlockState(pos.up(), block.getDefaultState());
                    }
                    stack.shrink(1);
                    return EnumActionResult.SUCCESS;
                }
            }
        }

        return EnumActionResult.PASS;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        List<Plant> plants = new LinkedList<>(PlantHandler.getPrehistoricPlantsAndTrees());
        Collections.sort(plants);
        if(this.isInCreativeTab(tab))
        for (Plant plant : plants) {
            if (plant.shouldRegister()) {
                subItems.add(new ItemStack(this, 1, PlantHandler.getPlantId(plant)));
            }
        }
    }
}
