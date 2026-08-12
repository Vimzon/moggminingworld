package com.mogg.miningworld;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Mogg Mining World - main mod entry point.
 *
 * Stage 0: project skeleton only.
 * No dimension, no teleportation, no ore generation yet.
 * Those are added in later stages per DEVELOPMENT_STATUS.md.
 */
@Mod(MoggMiningWorld.MOD_ID)
public class MoggMiningWorld {

    public static final String MOD_ID = "moggminingworld";
    private static final Logger LOGGER = LogManager.getLogger();

    public MoggMiningWorld() {
        LOGGER.info("Mogg Mining World is loading (Stage 0 - project skeleton, no gameplay features yet)");
    }
}
