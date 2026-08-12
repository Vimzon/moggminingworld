package com.mogg.miningworld.client;

import com.mogg.miningworld.MoggMiningWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Stage 2, step 2.5: registers {@link MiningWorldEffects} so the client
 * knows what to render for our dimension. The registration key
 * ("moggminingworld:mining_world") must match the "effects" field in
 * data/moggminingworld/dimension_type/mining_world.json exactly, or the
 * client silently falls back to overworld-style sky rendering.
 *
 * bus = MOD (not FORGE) because RegisterDimensionSpecialEffectsEvent is a
 * one-shot registration event, same family as RegisterEvent.
 * value = Dist.CLIENT so this class and its @SubscribeEvent method are
 * never loaded or invoked on a dedicated server.
 */
@Mod.EventBusSubscriber(modid = MoggMiningWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(
                new ResourceLocation(MoggMiningWorld.MOD_ID, "mining_world"),
                new MiningWorldEffects());
    }
}
