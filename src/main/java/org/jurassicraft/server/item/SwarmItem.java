package org.jurassicraft.server.item;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.tab.TabHandler;

import java.util.function.Supplier;

public class SwarmItem extends Item {
    private Supplier<IBlockState> block;

    public SwarmItem(Supplier<IBlockState> block) {
        super();
        this.block = block;
        this.setCreativeTab(TabHandler.ITEMS);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    	ItemStack stack = player.getHeldItem(hand);
        RayTraceResult result = this.rayTrace(world, player, true);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = result.getBlockPos();
            IBlockState state = world.getBlockState(pos);
            if (state.getMaterial() == Material.WATER) {
                if (player.canPlayerEdit(pos, Direction.UP, stack)) {
                    world.setBlockState(pos.up(), this.block.get());
                    stack.shrink(1);
                    return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }
}
