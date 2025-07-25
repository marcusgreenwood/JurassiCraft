package org.jurassicraft.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

public class NullRenderer extends Render<Entity> {

    public NullRenderer(RenderManager renderManager) {
	super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
	return null;
    }

}
