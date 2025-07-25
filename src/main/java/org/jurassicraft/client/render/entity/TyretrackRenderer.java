package org.jurassicraft.client.render.entity;

import java.util.Comparator;
import java.util.List;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.jurassicraft.JurassiCraft;
import org.jurassicraft.client.event.ClientEventHandler;
import org.jurassicraft.client.proxy.ClientProxy;
import org.jurassicraft.server.entity.vehicle.VehicleEntity;
import org.jurassicraft.server.entity.vehicle.util.WheelParticleData;
import org.lwjgl.opengl.GL11;
import com.google.common.collect.Lists;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
// TODO: Update to modern vertex format system
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
// TODO: Find correct client tick event import
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class TyretrackRenderer {
    
    // TODO: Update materials to use modern block material system
    public static final ResourceLocation TYRE_TRACKS_LOCATION = ResourceLocation.fromNamespaceAndPath(JurassiCraft.MODID, "textures/misc/tyre-tracks.png");
    
    private static final List<List<WheelParticleData>> DEAD_CARS_LISTS = Lists.newArrayList(); 
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderWorldLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        
    	final Minecraft mc = Minecraft.getInstance();
        Level world = mc.level;
        Player player = mc.player;
        Vec3 playerPos = player.position();
        
        // ... existing code ...
    }
    
    // TODO: This entire rendering system needs to be rewritten for 1.21
    // The old BufferBuilder API, Material system, and rendering approach are completely different
    /*
    @OnlyIn(Dist.CLIENT)
    private static void renderList(List<WheelParticleData> dataList, VertexConsumer buffer, float partialTicks, List<Pair<Long, Runnable>> list) {
        Level world = Minecraft.getInstance().level;
        // This method needs complete rewrite for modern rendering
    }
    
    private static boolean isStateAccepted(Level world, BlockPos position) {
        // Material system was removed, need to use BlockState properties instead
        return false; // Placeholder until proper implementation
    }
    */
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    // TODO: Fix for proper client tick event
    public static void onWorldTick(Object event) {
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