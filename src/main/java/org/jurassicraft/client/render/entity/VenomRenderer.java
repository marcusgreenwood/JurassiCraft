package org.jurassicraft.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.server.entity.VenomEntity;

public class VenomRenderer implements IRenderFactory<VenomEntity> {
	
    @Override
    public Render<? super VenomEntity> createRenderFor(RenderManager manager) {
        return new Renderer(manager);
    }

    public static class Renderer extends Render<VenomEntity> {
        protected Renderer(RenderManager renderManager) {
            super(renderManager);
        }

        @Override
        protected ResourceLocation getEntityTexture(VenomEntity entity) {
            return null;
        }
    }
}
