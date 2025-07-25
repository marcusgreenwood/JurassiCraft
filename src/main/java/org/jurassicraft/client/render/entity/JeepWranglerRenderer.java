package org.jurassicraft.client.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.client.model.animation.entity.vehicle.CarAnimator;
import org.jurassicraft.server.entity.vehicle.JeepWranglerEntity;

public class JeepWranglerRenderer extends CarRenderer<JeepWranglerEntity> {

    public JeepWranglerRenderer(RenderManager manager) {
        super(manager, "jeep_wrangler");
    }

    @Override
    protected CarAnimator createCarAnimator() {
        return new CarAnimator()
                .addDoor(new CarAnimator.Door("door left main", 0, true))
                .addDoor(new CarAnimator.Door("door right main", 1, false));
    }
}