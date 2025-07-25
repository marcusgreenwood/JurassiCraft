package org.jurassicraft.server.block.tree;

import net.minecraft.world.level.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jurassicraft.server.item.ItemHandler;

import java.util.Locale;
import java.util.Random;

public class AncientDoorBlock extends BlockDoor {
    private TreeType treeType;

    public AncientDoorBlock(TreeType treeType) {
        super(Material.WOOD);
        this.treeType = treeType;
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setSoundType(SoundType.WOOD);
        this.setUnlocalizedName(treeType.name().toLowerCase(Locale.ENGLISH) + "_door");
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (state.getValue(HALF) == EnumDoorHalf.UPPER) {
            return null;
        } else {
            return this.getItem();
        }
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(this.getItem());
    }

    private Item getItem() {
        return ItemHandler.ANCIENT_DOORS.get(this.treeType);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
}
