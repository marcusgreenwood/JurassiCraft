package org.jurassicraft.client.render.entity;

import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.server.entity.vehicle.VehicleEntity;
import org.jurassicraft.server.entity.vehicle.util.WheelParticleData;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = JurassiCraft.MODID, value = Dist.CLIENT)
public class TyretrackRenderer {
    
    // Updated material checking for 1.21 - using MapColor instead of old Material system
    public static final List<MapColor> ALLOWED_MATERIALS = Lists.newArrayList(
        MapColor.GRASS, MapColor.DIRT, MapColor.SAND
    );
    
    public static final ResourceLocation TYRE_TRACKS_LOCATION = ResourceLocation.fromNamespaceAndPath(
        JurassiCraft.MODID, "textures/misc/tyre-tracks.png"
    );
    
    private static final List<List<WheelParticleData>> DEAD_CARS_LISTS = Lists.newArrayList(); 
    
    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        
        final Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        Player player = mc.player;
        
        if (level == null || player == null) {
            return;
        }
        
        Vec3 playerPos = player.position();
        
        // Modern rendering setup
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TYRE_TRACKS_LOCATION);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        
        // Camera position calculation for 1.21
        Vec3 cameraPos = event.getCamera().getPosition();
        
        List<Pair<Long, Runnable>> runList = Lists.newArrayList();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        
        for(Entity entity : level.getAllEntities()) {
            if(entity instanceof VehicleEntity car) {
                for(List<WheelParticleData> list : car.wheelDataList) {
                    renderList(list, buffer, event.getPartialTick().getRealtimeDeltaTicks(), runList, cameraPos);
                }
            }
        }
        
        DEAD_CARS_LISTS.forEach(list -> 
            renderList(list, buffer, event.getPartialTick().getRealtimeDeltaTicks(), runList, cameraPos)
        );
        
        runList.sort((o1, o2) -> Comparator.<Long>naturalOrder().compare(o2.getLeft(), o1.getLeft()));
        
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        runList.forEach(pair -> pair.getRight().run());
        poseStack.popPose();
        
        BufferUploader.drawWithShader(buffer.end());
            
        RenderSystem.disableBlend();
    }
    
    private static void renderList(List<WheelParticleData> dataList, BufferBuilder buffer, 
                                 float partialTicks, List<Pair<Long, Runnable>> list, Vec3 cameraPos) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        
        for(int i = 0; i < dataList.size() - 1; i++) {
            WheelParticleData start = dataList.get(i);
            if(!start.shouldRender()) {
                continue;
            }
            WheelParticleData end = dataList.get(i + 1);
                
            Vec3 sv = start.getPosition();
            Vec3 ev = end.getPosition();
            
            Vec3 startOpposite = start.getOppositePosition();
            Vec3 endOpposite = end.getOppositePosition();
            
            BlockPos position = BlockPos.containing(sv);
            BlockPos endPosition = BlockPos.containing(ev);
            
            if(sv.y != ev.y || !isStateAccepted(level, position) || !isStateAccepted(level, endPosition)) {
                continue;
            }
            
            final int n = i;
            list.add(Pair.of(start.getWorldTime(), () -> {
                double d = 1D / Math.sqrt(Math.pow(sv.x - startOpposite.x, 2) + Math.pow(sv.z - startOpposite.z, 2)) / 2D;    
                Vec3 vec = new Vec3((sv.x - startOpposite.x) * d, 0, (sv.z - startOpposite.z) * d);
                
                double d1 = 1D / Math.sqrt(Math.pow(ev.x - endOpposite.x, 2) + Math.pow(ev.z - endOpposite.z, 2)) / 2D;    
                Vec3 vec1 = new Vec3((ev.x - endOpposite.x) * d, 0, (ev.z - endOpposite.z) * d);
                
                // Updated light calculation for 1.21
                float sl = level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, position);
                float el = level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, endPosition);
                    
                float sa = start.getAlpha(partialTicks);
                float ea = end.getAlpha(partialTicks);
                                    
                double offset = (n + 2) * 0.0001D; // No z-fighting
                
                // Relative to camera position
                double relX1 = sv.x - cameraPos.x;
                double relY1 = sv.y - cameraPos.y + offset;
                double relZ1 = sv.z - cameraPos.z;
                double relX2 = ev.x - cameraPos.x;
                double relY2 = ev.y - cameraPos.y + offset;
                double relZ2 = ev.z - cameraPos.z;
                
                buffer.vertex(relX1 + vec.x / 2D, relY1, relZ1 + vec.z / 2D).uv(0, 0).color(sl, sl, sl, sa).endVertex();
                buffer.vertex(relX1 - vec.x / 2D, relY1, relZ1 - vec.z / 2D).uv(0, 1).color(sl, sl, sl, sa).endVertex();
                buffer.vertex(relX2 - vec1.x / 2D, relY2, relZ2 - vec1.z / 2D).uv(1, 1).color(el, el, el, ea).endVertex();
                buffer.vertex(relX2 + vec1.x / 2D, relY2, relZ2 + vec1.z / 2D).uv(1, 0).color(el, el, el, ea).endVertex();
                    
                // Flip quad for double-sided rendering
                buffer.vertex(relX1 + vec.x / 2D, relY1, relZ1 + vec.z / 2D).uv(0, 0).color(sl, sl, sl, sa).endVertex();
                buffer.vertex(relX2 + vec1.x / 2D, relY2, relZ2 + vec1.z / 2D).uv(1, 0).color(el, el, el, ea).endVertex();
                buffer.vertex(relX2 - vec1.x / 2D, relY2, relZ2 - vec1.z / 2D).uv(1, 1).color(el, el, el, ea).endVertex();
                buffer.vertex(relX1 - vec.x / 2D, relY1, relZ1 - vec.z / 2D).uv(0, 1).color(sl, sl, sl, sa).endVertex();
            }));
        }
    }
    
    private static boolean isStateAccepted(Level level, BlockPos position) {
        BlockPos downPos = position.below();
        BlockState downState = level.getBlockState(downPos);
        BlockState currentState = level.getBlockState(position);
        
        return !currentState.liquid() && 
               downState.isFaceSturdy(level, downPos, Direction.UP) && 
               ALLOWED_MATERIALS.contains(downState.getMapColor(level, downPos));
    }
    
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        
        List<List<WheelParticleData>> emptyLists = Lists.newArrayList();
        DEAD_CARS_LISTS.forEach(list -> {
            List<WheelParticleData> markedRemoved = Lists.newArrayList();
            list.forEach(wheel -> wheel.onUpdate(markedRemoved));
            markedRemoved.forEach(list::remove);
            markedRemoved.clear();
            if(list.isEmpty()) {
                emptyLists.add(list);
            }
        });
        emptyLists.forEach(DEAD_CARS_LISTS::remove);
    }
    
    public static void uploadList(VehicleEntity entity) {
        if(entity.level().isClientSide) {
            for(List<WheelParticleData> list : entity.wheelDataList) {
                DEAD_CARS_LISTS.add(0, list);
            }
        }
    }
}