package org.jurassicraft.server.block.tree;

import net.minecraft.world.level.block.BlockFence;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.world.level.material.MapColor;
import org.jurassicraft.server.tab.TabHandler;

import java.util.Locale;

public class AncientFenceBlock extends BlockFence {
    public AncientFenceBlock(TreeType treeType) {
        super(Material.WOOD, MapColor.BROWN);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setSoundType(SoundType.WOOD);
        this.setUnlocalizedName(treeType.name().toLowerCase(Locale.ENGLISH) + "_fence");
        this.setCreativeTab(TabHandler.PLANTS);
    }
}
