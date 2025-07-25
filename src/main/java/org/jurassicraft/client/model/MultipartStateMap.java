package org.jurassicraft.client.model;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

import java.util.Collections;

public class MultipartStateMap extends StateMapperBase {
	
    @Override
    protected ModelResourceLocation getModelResourceLocation(final IBlockState state) {
    	final String blockName = Block.REGISTRY.getNameForObject(state.getBlock()).toString();
        return new ModelResourceLocation(blockName, this.getPropertyString(Collections.emptyMap()));
    }
}
