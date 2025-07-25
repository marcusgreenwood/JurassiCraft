package org.jurassicraft.server.plant;

import org.jurassicraft.server.block.BlockHandler;

import net.minecraft.world.level.block.Block;

/**
 * Created by Codyr on 29/10/2016.
 */
public class GraminiditesBambusoidesPlant extends Plant {
    @Override
    public String getName() {
        return "Graminidites Bambusoides";
    }

    @Override
    public Block getBlock() { return BlockHandler.GRAMINIDITES_BAMBUSOIDES; }


    @Override
    public int getHealAmount() {
        return 4000;
    }
}

