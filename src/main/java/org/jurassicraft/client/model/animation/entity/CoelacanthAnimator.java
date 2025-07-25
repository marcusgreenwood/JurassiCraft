package org.jurassicraft.client.model.animation.entity;

import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.client.model.AnimatableModel;
import org.jurassicraft.client.model.animation.EntityAnimator;
import org.jurassicraft.server.entity.dinosaur.CoelacanthEntity;

public class CoelacanthAnimator extends EntityAnimator<CoelacanthEntity> {
	
    @Override
    protected void performAnimations(AnimatableModel model, CoelacanthEntity entity, float limbSwing, float limbSwingAmount, float ticks, float rotationYaw, float rotationPitch, float scale) {
        
    	final AdvancedModelRenderer neck = model.getCube("Neck ");
    	final AdvancedModelRenderer body1 = model.getCube("Body Section 1");
    	final AdvancedModelRenderer body2 = model.getCube("Body Section 2");
    	final AdvancedModelRenderer body3 = model.getCube("Body Section 3");
    	final AdvancedModelRenderer tail1 = model.getCube("Tail Section 1");
    	final AdvancedModelRenderer tail2 = model.getCube("Tail Section 2");
    	final AdvancedModelRenderer tail3 = model.getCube("Tail Section 3");
    	final AdvancedModelRenderer leftFlipper = model.getCube("Left Front Flipper");
    	final AdvancedModelRenderer rightFlipper = model.getCube("Right Front Flipper");
    	final AdvancedModelRenderer lowerJawFront = model.getCube("Lower jaw front");

        final AdvancedModelRenderer[] body = new AdvancedModelRenderer[] { tail3, tail2, tail1, body3, body2, body1 };
        final AdvancedModelRenderer[] frontBody = new AdvancedModelRenderer[] { body3, body2, body1 };

        model.chainSwing(frontBody, 0.6F, 0.4F, 3.0D, limbSwing, limbSwingAmount);

        if (entity.isInWater()) {
            model.walk(leftFlipper, 0.2F, 0.25F, false, 1.0F, 0.1F, ticks, 0.25F);
            model.walk(rightFlipper, 0.2F, 0.25F, false, 1.0F, 0.1F, ticks, 0.25F);
            model.chainSwing(body, 0.05F, -0.075F, 1.5D, ticks, 0.25F);

            model.bob(neck, 0.125F, 4.5F, false, ticks, 0.25F);
            model.walk(lowerJawFront, 0.1F, 0.7F, false, 0.0F, 0.5F, ticks, 0.25F);
        }

        entity.tailBuffer.applyChainSwingBuffer(body);
    }
}
