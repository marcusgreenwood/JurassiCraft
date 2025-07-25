package org.jurassicraft.client.render.entity;

import org.jurassicraft.client.model.animation.entity.vehicle.HelicopterAnimator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;

public class HeliRenderer extends HelicopterRenderer {

    public HeliRenderer(RenderManager manager) {
        super(manager, "helicopter");
    }

    @Override
    protected HelicopterAnimator createCarAnimator() {
        return new HelicopterAnimator();
    }
}