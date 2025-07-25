package org.jurassicraft.server.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import org.jurassicraft.server.api.IncubatorEnvironmentItem;
import org.jurassicraft.server.tab.TabHandler;

public class PeatMossBlock extends Block implements IncubatorEnvironmentItem {
    public PeatMossBlock() {
        super(Material.GROUND);
        this.setSoundType(SoundType.GROUND);
        this.setHardness(0.5F);
        this.setCreativeTab(TabHandler.PLANTS);
    }
}
