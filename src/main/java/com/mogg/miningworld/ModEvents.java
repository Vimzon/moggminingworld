package com.mogg.miningworld;

import com.mogg.miningworld.command.PregenCommand;
import com.mogg.miningworld.pregen.PregenManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Stage 1: this class only exists to prove the dimension registered and
 * loaded correctly, by checking for it right after the server starts and
 * writing a clear line to the server log either way.
 *
 * Stage 7: registers the /moggminingworld pregen command and drives chunk
 * pre-generation from the server tick.
 */
@Mod.EventBusSubscriber(modid = MoggMiningWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        ServerLevel miningWorld = server.getLevel(MoggMiningWorld.MINING_WORLD_KEY);

        if (miningWorld != null) {
            MoggMiningWorld.LOGGER.info(
                    "[Mogg Mining World] Mining World dimension is registered and loaded: {}",
                    MoggMiningWorld.MINING_WORLD_KEY.location());
        } else {
            MoggMiningWorld.LOGGER.warn(
                    "[Mogg Mining World] Mining World dimension was NOT found at server start! " +
                            "Check data/moggminingworld/dimension/mining_world.json and dimension_type/mining_world.json for errors.");
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        PregenCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            PregenManager.tick();
        }
    }
}