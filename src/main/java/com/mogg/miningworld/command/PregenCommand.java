package com.mogg.miningworld.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mogg.miningworld.MoggMiningWorld;
import com.mogg.miningworld.pregen.PregenManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;

/**
 * Stage 7: commands for the Mining World.
 *
 * /moggminingworld pregen start [radius]
 *   - Starts chunk pre-generation for the Mining World. Without an argument
 *     it uses the current world border (set via DimWorldBorder's
 *     /dimworldborder command); with a radius it generates a square around
 *     the border center.
 * /moggminingworld pregen stop
 * /moggminingworld pregen status
 */
public class PregenCommand {

    private static final SimpleCommandExceptionType ERROR_NO_MINING_WORLD =
            new SimpleCommandExceptionType(Component.literal("Mining World dimension is not loaded."));
    private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING =
            new SimpleCommandExceptionType(Component.literal("Pregen is already running. Use /moggminingworld pregen stop first."));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("moggminingworld")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("pregen")
                        .then(Commands.literal("start")
                                .executes(context -> start(context.getSource(), -1))
                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 60000000))
                                        .executes(context -> start(context.getSource(), IntegerArgumentType.getInteger(context, "radius")))))
                        .then(Commands.literal("stop")
                                .executes(context -> stop(context.getSource())))
                        .then(Commands.literal("status")
                                .executes(context -> status(context.getSource())))));
    }

    private static int start(CommandSourceStack source, int radiusBlocks) throws CommandSyntaxException {
        ServerLevel miningWorld = source.getServer().getLevel(MoggMiningWorld.MINING_WORLD_KEY);
        if (miningWorld == null) {
            throw ERROR_NO_MINING_WORLD.create();
        }
        if (PregenManager.isRunning()) {
            throw ERROR_ALREADY_RUNNING.create();
        }
        WorldBorder border = miningWorld.getWorldBorder();
        double size = radiusBlocks > 0 ? radiusBlocks * 2.0D : border.getSize();
        PregenManager.start(miningWorld, size, border.getCenterX(), border.getCenterZ());
        source.sendSuccess(() -> Component.literal("Mining World pregen started: " + PregenManager.getTotalChunks() + " chunks"), true);
        return 1;
    }

    private static int stop(CommandSourceStack source) {
        if (!PregenManager.isRunning()) {
            source.sendSuccess(() -> Component.literal("No pregen is currently running."), false);
            return 0;
        }
        int done = PregenManager.getGeneratedChunks();
        int total = PregenManager.getTotalChunks();
        PregenManager.stop();
        source.sendSuccess(() -> Component.literal("Pregen stopped: " + done + "/" + total + " chunks"), true);
        return 1;
    }

    private static int status(CommandSourceStack source) {
        if (!PregenManager.isRunning()) {
            source.sendSuccess(() -> Component.literal("Pregen is not running."), false);
            return 0;
        }
        int done = PregenManager.getGeneratedChunks();
        int total = PregenManager.getTotalChunks();
        int percent = total > 0 ? done * 100 / total : 0;
        source.sendSuccess(() -> Component.literal("Pregen: " + done + "/" + total + " chunks (" + percent + "%)"), false);
        return 1;
    }
}