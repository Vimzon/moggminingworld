package com.mogg.miningworld;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mogg Mining World - main mod entry point.
 *
 * Stage 1: registers the Mining World dimension (via datapack JSON under
 * src/main/resources/data/moggminingworld/dimension and dimension_type).
 * Generation is still a simple flat placeholder - real cave generation is
 * Stage 2. No teleport block yet - that is Stage 6.
 */
@Mod(MoggMiningWorld.MOD_ID)
public class MoggMiningWorld {

    public static final String MOD_ID = "moggminingworld";
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Resource key identifying the Mining World dimension.
     * Backed by data/moggminingworld/dimension/mining_world.json
     */
    public static final ResourceKey<Level> MINING_WORLD_KEY =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(MOD_ID, "mining_world"));

    public MoggMiningWorld() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        com.mogg.miningworld.worldgen.ModWorldGen.FEATURES.register(modBus);
        LOGGER.info("Mogg Mining World is loading (Stage 1 - dimension registration)");
    }
}
