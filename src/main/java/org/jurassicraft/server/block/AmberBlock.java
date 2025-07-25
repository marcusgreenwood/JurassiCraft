package org.jurassicraft.server.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.tab.TabHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AmberBlock extends Block {
    public AmberBlock() {
        super(Material.ROCK);
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setCreativeTab(TabHandler.BLOCKS);
        this.setHarvestLevel("pickaxe", 2);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> ret = new ArrayList<>();

        Random rand = world instanceof World ? ((World) world).rand : RANDOM;

        int count = rand.nextInt(fortune + 1) - 1;

        if (count < 0) {
            count = 0;
        }

        for (int i = 0; i < count + 1; i++) {
            Item item = ItemHandler.AMBER;

            if (item != null) {
                ret.add(new ItemStack(item, 1, rand.nextBoolean() ? 1 : 0));
            }
        }

        return ret;
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }
}
