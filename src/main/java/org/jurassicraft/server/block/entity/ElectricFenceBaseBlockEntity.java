package org.jurassicraft.server.block.entity;

import net.minecraft.tileentity.TileEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

public class ElectricFenceBaseBlockEntity extends TileEntity {
    @Override
    public double getMaxRenderDistanceSquared() {
        return 16384.0;
    }
}
