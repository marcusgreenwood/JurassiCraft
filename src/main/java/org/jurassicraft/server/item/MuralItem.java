package org.jurassicraft.server.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.entity.item.MuralEntity;
import org.jurassicraft.server.tab.TabHandler;

public class MuralItem extends Item {
    public MuralItem() {
        this.setCreativeTab(TabHandler.DECORATIONS);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	ItemStack stack = player.getHeldItem(hand);
        if (side != Direction.DOWN && side != Direction.UP) {
            BlockPos offset = pos.offset(side);

            if (player.canPlayerEdit(offset, side, stack)) {
                MuralEntity mural = new MuralEntity(world, offset, side);
                if (mural.onValidSurface()) {
                    if (!world.isRemote) {
                        world.spawnEntity(mural);
                    }

                    stack.shrink(1);

                    return EnumActionResult.SUCCESS;
                }
            }
        }

        return EnumActionResult.PASS;
    }
}
