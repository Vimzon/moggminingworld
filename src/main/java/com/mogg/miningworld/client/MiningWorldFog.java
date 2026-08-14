package com.mogg.miningworld.client;

import com.mogg.miningworld.MoggMiningWorld;
import net.minecraft.client.Camera;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Stage 10: depth fog for the Mining World.
 *
 * The deeper the camera is, the denser the fog becomes (shorter far plane
 * distance) and the darker/murkier its color, giving an "underground
 * pressure / depth" atmosphere. Only applies in the Mining World dimension;
 * vanilla dimensions are untouched.
 */
@Mod.EventBusSubscriber(modid = MoggMiningWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MiningWorldFog {

    private static final int SURFACE_Y = 320;

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Camera camera = event.getCamera();
        if (camera.getEntity() == null
                || camera.getEntity().level().dimension() != MoggMiningWorld.MINING_WORLD_KEY) {
            return;
        }
        float depth = depthFactor(camera);
        float far = event.getFarPlaneDistance();
        float near = event.getNearPlaneDistance();
        event.setFarPlaneDistance(far * (1.0F - 0.75F * depth));
        event.setNearPlaneDistance(near * (1.0F - 0.75F * depth));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Camera camera = event.getCamera();
        if (camera.getEntity() == null
                || camera.getEntity().level().dimension() != MoggMiningWorld.MINING_WORLD_KEY) {
            return;
        }
        float depth = depthFactor(camera);
        float r = 0.20F - 0.15F * depth;
        float g = 0.20F - 0.15F * depth;
        float b = 0.26F - 0.18F * depth;
        event.setRed(r);
        event.setGreen(g);
        event.setBlue(b);
    }

    private static float depthFactor(Camera camera) {
        int y = camera.getBlockPosition().getY();
        float depth = 1.0F - (float) y / (float) SURFACE_Y;
        return Math.max(0.0F, Math.min(1.0F, depth));
    }
}