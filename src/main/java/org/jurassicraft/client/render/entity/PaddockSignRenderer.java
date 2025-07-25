package org.jurassicraft.client.render.entity;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.DistOnly;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.dinosaur.Dinosaur;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.entity.item.PaddockSignEntity;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaddockSignRenderer implements IRenderFactory<PaddockSignEntity> {
	
    @Override
    public Render<? super PaddockSignEntity> createRenderFor(RenderManager manager) {
        return new Renderer(manager);
    }

    public static class Renderer extends Render<PaddockSignEntity> {
        private static int DISPLAY_LIST = -1;
        private static boolean HAS_COMPILED = false;
        private final Map<Integer, ResourceLocation> TEXTURES = new HashMap<>();

        public Renderer(RenderManager manager) {
            super(manager);
        }

        @Override
        public void doRender(PaddockSignEntity entity, double x, double y, double z, float yaw, float partialTicks) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(180.0F - yaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.disableCull();
            this.bindEntityTexture(entity);

            int id = entity.getDinosaur();

            ResourceLocation texture = this.TEXTURES.get(id);

            if (texture == null) {
            	Dinosaur dinosaur = EntityHandler.getDinosaurById(id);
                ResourceLocation identifier = dinosaur.getIdentifier();
                texture = new ResourceLocation(identifier.getResourceDomain(), "textures/paddock/" + identifier.getResourcePath() + ".png");
                this.TEXTURES.put(id, texture);
            }

            this.bindTexture(texture);

            if (HAS_COMPILED) {
                GlStateManager.callList(DISPLAY_LIST);
            } else {
                DISPLAY_LIST = GLAllocation.generateDisplayLists(1);
                GlStateManager.glNewList(DISPLAY_LIST, GL11.GL_COMPILE);
                float scale = 0.0625F;
                GlStateManager.scale(scale, scale, scale);

                this.renderLayer(entity, entity.getWidthPixels(), entity.getHeightPixels(), entity.getWidthPixels(), entity.getHeightPixels(), false);
                GlStateManager.glEndList();

                HAS_COMPILED = true;
            }

            GlStateManager.enableCull();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, yaw, partialTicks);
        }

        @Override
        protected ResourceLocation getEntityTexture(PaddockSignEntity sign) {
            return null;
        }

        private void renderLayer(PaddockSignEntity entity, int width, int height, int textureWidth, int textureHeight, boolean lighting) {
            float centerWidth = (float) -textureWidth / 2.0F;
            float centerHeight = (float) -textureHeight / 2.0F;
            float pixelSize = 0.0625F;
            float depth = 0.5F;
            GlStateManager.translate(0.0F, 0.0F, -depth + 0.5F);

            for (int x = 0; x < textureWidth * pixelSize; x++) {
                for (int y = 0; y < textureHeight * pixelSize; y++) {
                    float maxX = centerWidth + (x + 1) / pixelSize;
                    float minX = centerWidth + x / pixelSize;
                    float maxY = centerHeight + (y + 1) / pixelSize;
                    float minY = centerHeight + y / pixelSize;
                    if(lighting) {
                    	this.setLightmap(entity, (maxX + minX) / 2.0F, (maxY + minY) / 2.0F);
                    }else {
                    float maxTextureX = (textureWidth - x / pixelSize) / textureWidth;
                    float minTextureX = (textureWidth - (x + 1) / pixelSize) / textureWidth;
                    float maxTextureY = (textureHeight - y / pixelSize) / textureHeight;
                    float minTextureY = (textureHeight - (y + 1) / pixelSize) / textureHeight;
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder buffer = tessellator.getBuffer();

                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
                    buffer.pos(maxX, minY, 0.0F).tex(minTextureX, maxTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                    buffer.pos(minX, minY, 0.0F).tex(maxTextureX, maxTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                    buffer.pos(minX, maxY, 0.0F).tex(maxTextureX, minTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                    buffer.pos(maxX, maxY, 0.0F).tex(minTextureX, minTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                    buffer.pos(maxX, minY, depth).tex(minTextureX, maxTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                    buffer.pos(minX, minY, depth).tex(maxTextureX, maxTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                    buffer.pos(minX, maxY, depth).tex(maxTextureX, minTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                    buffer.pos(maxX, maxY, depth).tex(minTextureX, minTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                    tessellator.draw();

                    for (float i = minX + 0.125F; i < maxX + 0.125F; i += 0.125F) {
                        maxTextureX = (centerWidth - i) / textureWidth;
                        minTextureX = (centerWidth - (i - pixelSize)) / textureWidth;
                        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
                        buffer.pos(i, minY, 0.0F).tex(maxTextureX, maxTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(i, minY, depth).tex(maxTextureX, maxTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(i, maxY, depth).tex(minTextureX, minTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(i, maxY, 0.0F).tex(minTextureX, minTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                        tessellator.draw();
                        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
                        buffer.pos(i - 0.125F, minY, 0.0F).tex(maxTextureX, maxTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(i - 0.125F, minY, depth).tex(maxTextureX, maxTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(i - 0.125F, maxY, depth).tex(minTextureX, minTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(i - 0.125F, maxY, 0.0F).tex(minTextureX, minTextureY).normal(0.0F, 0.0F, -1.0F).endVertex();
                        tessellator.draw();
                    }

                    maxTextureX = (textureWidth - x / pixelSize) / textureWidth;
                    minTextureX = (textureWidth - (x + 1) / pixelSize) / textureWidth;

                    for (float i = minY; i < maxY; i += 0.125F) {
                        maxTextureY = (textureHeight - i) / (textureHeight);
                        minTextureY = (textureHeight - (i + 0.125F)) / (textureHeight);
                        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
                        buffer.pos(minX, i, 0.0F).tex(maxTextureX, maxTextureY + 0.5F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(minX, i, depth).tex(maxTextureX, minTextureY + 0.5F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(maxX, i, depth).tex(minTextureX, minTextureY + 0.5F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(maxX, i, 0.0F).tex(minTextureX, maxTextureY + 0.5F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        tessellator.draw();
                        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
                        buffer.pos(minX, i + 0.125F, 0.0F).tex(maxTextureX, maxTextureY + 0.5F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(minX, i + 0.125F, depth).tex(maxTextureX, minTextureY + 0.5F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(maxX, i + 0.125F, depth).tex(minTextureX, minTextureY + 0.5F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        buffer.pos(maxX, i + 0.125F, 0.0F).tex(minTextureX, maxTextureY + 0.5F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        tessellator.draw();
                    }
                    }
                }
            }
        }

        private void setLightmap(PaddockSignEntity sign, float xzOffset, float yOffset) {
            int posX = MathHelper.floor(sign.getX());
            int posY = MathHelper.floor(sign.getY() + (yOffset / 16.0F));
            int posZ = MathHelper.floor(sign.getZ());

            EnumFacing direction = sign.facingDirection;

            if (direction == EnumFacing.NORTH) {
                posX = MathHelper.floor(sign.getX() + (xzOffset / 16.0F));
            } else if (direction == EnumFacing.WEST) {
                posZ = MathHelper.floor(sign.getZ() - (xzOffset / 16.0F));
            } else if (direction == EnumFacing.SOUTH) {
                posX = MathHelper.floor(sign.getX() - (xzOffset / 16.0F));
            } else if (direction == EnumFacing.EAST) {
                posZ = MathHelper.floor(sign.getZ() + (xzOffset / 16.0F));
            }

            int combinedLight = this.renderManager.world.getCombinedLight(new BlockPos(posX, posY, posZ), 0);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, combinedLight % 65536, combinedLight / 65536.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
        }
    }
}