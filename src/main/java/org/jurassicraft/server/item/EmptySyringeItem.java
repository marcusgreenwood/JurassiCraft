package org.jurassicraft.server.item;

import net.minecraft.world.level.block.BlockBush;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.tab.TabHandler;

public class EmptySyringeItem extends Item {
    public EmptySyringeItem() {
        super();
        this.setCreativeTab(TabHandler.ITEMS);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	ItemStack stack = player.getHeldItem(hand);
        if (world.getBlockState(pos).getBlock() instanceof BlockBush) {
            if (stack.getCount() == 1) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(ItemHandler.PLANT_CELLS));
            } else {
                player.inventory.addItemStackToInventory(new ItemStack(ItemHandler.PLANT_CELLS));
                stack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }
}
