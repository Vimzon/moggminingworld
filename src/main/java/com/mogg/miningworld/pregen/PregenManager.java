package com.mogg.miningworld.pregen;

import com.mogg.miningworld.MoggMiningWorld;
import com.mogg.miningworld.config.MiningWorldConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Stage 7: chunk pre-generation for the Mining World.
 *
 * Builds a queue of chunk coordinates (from the world border of the Mining
 * World or an explicit radius) and generates them a few per server tick, so
 * the whole limited area is fully generated ahead of time (works together
 * with DimWorldBorder: admin sets the border via /dimworldborder, then
 * pre-generates the area inside it).
 *
 * Stage 9: chunks per tick comes from the config (pregen.chunks_per_tick).
 */
public class PregenManager {

    private static final Deque<ChunkPos> QUEUE = new ArrayDeque<>();
    private static ServerLevel miningWorld;
    private static int totalChunks = 0;
    private static int generatedChunks = 0;
    private static boolean running = false;

    private PregenManager() {
    }

    public static boolean isRunning() {
        return running;
    }

    public static int getTotalChunks() {
        return totalChunks;
    }

    public static int getGeneratedChunks() {
        return generatedChunks;
    }

    public static void start(ServerLevel level, double size, double centerX, double centerZ) {
        stop();
        miningWorld = level;
        QUEUE.clear();
        generatedChunks = 0;

        int halfChunks = (int) Math.ceil(size / 2.0 / 16.0);
        int centerChunkX = Math.floorDiv((int) Math.floor(centerX), 16);
        int centerChunkZ = Math.floorDiv((int) Math.floor(centerZ), 16);

        for (int cx = centerChunkX - halfChunks; cx <= centerChunkX + halfChunks; cx++) {
            for (int cz = centerChunkZ - halfChunks; cz <= centerChunkZ + halfChunks; cz++) {
                QUEUE.add(new ChunkPos(cx, cz));
            }
        }
        totalChunks = QUEUE.size();
        running = true;
        MoggMiningWorld.LOGGER.info("[Mogg] Pregen started: {} chunks in mining world", totalChunks);
    }

    public static void stop() {
        if (running) {
            MoggMiningWorld.LOGGER.info("[Mogg] Pregen stopped: {}/{} chunks done", generatedChunks, totalChunks);
        }
        running = false;
        QUEUE.clear();
        miningWorld = null;
        totalChunks = 0;
        generatedChunks = 0;
    }

    /** Called once per server tick (server thread). Generates a small batch. */
    public static void tick() {
        if (!running || miningWorld == null) {
            return;
        }
        int chunksPerTick = MiningWorldConfig.pregenChunksPerTick();
        for (int i = 0; i < chunksPerTick && !QUEUE.isEmpty(); i++) {
            ChunkPos pos = QUEUE.poll();
            miningWorld.getChunk(pos.x, pos.z, ChunkStatus.FULL, true);
            generatedChunks++;
        }
        if (generatedChunks % 100 == 0 && totalChunks > 0) {
            int percent = (int) (generatedChunks * 100.0 / totalChunks);
            MoggMiningWorld.LOGGER.info("[Mogg] Pregen progress: {}/{} chunks ({}%)", generatedChunks, totalChunks, percent);
        }
        if (QUEUE.isEmpty()) {
            MoggMiningWorld.LOGGER.info("[Mogg] Pregen finished: {} chunks generated", generatedChunks);
            running = false;
        }
    }
}