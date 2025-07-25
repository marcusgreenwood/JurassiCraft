package org.jurassicraft.server.block;

import org.jurassicraft.server.tab.TabHandler;

import net.minecraft.world.level.block.BlockDoor;
import net.minecraft.world.level.material.MapColor;

public class JPMainGateBlock extends BlockDoor {
    public JPMainGateBlock() {
        super(Material.ROCK);
        this.setCreativeTab(TabHandler.BLOCKS);
        this.setHardness(1.5F);
        this.setResistance(1.5F);
    }
}
