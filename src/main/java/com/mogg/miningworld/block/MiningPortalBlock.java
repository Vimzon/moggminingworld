package com.mogg.miningworld.block;

import com.mogg.miningworld.MoggMiningWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Stage 6: right-click teleport block.
 *
 * Behavior:
 * - In the Overworld: right-click teleports the player to Mining World.
 * - In Mining World: right-click teleports the player back to the Overworld
 *   spawn point.
 * - No crafting / no loot; admin places it with /give.
 */
public class MiningPortalBlock extends Block {

    public MiningPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (level.dimension() == Level.OVERWORLD) {
                ServerLevel miningWorld = serverPlayer.server.getLevel(MoggMiningWorld.MINING_WORLD_KEY);
                if (miningWorld != null) {
                    Vec3 target = findSafeSpawn(miningWorld, 0, 100, 0);
                    serverPlayer.teleportTo(miningWorld, target.x, target.y, target.z, serverPlayer.getYRot(), serverPlayer.getXRot());
                    return InteractionResult.CONSUME;
                }
            } else if (level.dimension() == MoggMiningWorld.MINING_WORLD_KEY) {
                ServerLevel overworld = serverPlayer.server.overworld();
                Vec3 target = findSafeSpawn(overworld, 0, 100, 0);
                serverPlayer.teleportTo(overworld, target.x, target.y, target.z, serverPlayer.getYRot(), serverPlayer.getXRot());
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * Finds a safe spawn spot near (x, y, z): scans a vertical column down
     * from the given height and picks the first position with a solid block
     * below and at least two air blocks above (so the player doesn't
     * suffocate or fall into the void).
     */
    private static Vec3 findSafeSpawn(ServerLevel level, int x, int y, int z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dy = y; dy > level.getMinBuildHeight() + 3; dy--) {
            pos.set(x, dy, z);
            if (level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above()) && level.getBlockState(pos.below()).isSolid()) {
                return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            }
        }
        // Fallback: if nothing found, teleport to world spawn height of the dimension.
        return new Vec3(x + 0.5, y + 0.5, z + 0.5);
    }
}
