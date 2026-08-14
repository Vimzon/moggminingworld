package com.mogg.miningworld.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Stage 9: TOML configuration (auto-generated as moggminingworld-common.toml).
 *
 * Sections:
 * - Dynamic modded ores (Stage 5): per-tier height ranges, vein size, weight,
 *   global frequency multiplier (veins per chunk) and size multiplier.
 * - Pregen (Stage 7): chunks generated per server tick.
 * - Dig limit: optional rule that blocks breaking blocks above a max Y in
 *   the Mining World ("limit digging up").
 */
public class MiningWorldConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // ---- Dynamic modded ores (Stage 5) ----
    private static final ForgeConfigSpec.ConfigValue<Integer> MODDED_ORE_VEINS_PER_CHUNK;
    private static final ForgeConfigSpec.ConfigValue<Double> MODDED_ORE_SIZE_MULTIPLIER;

    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_COMMON_MIN_Y;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_COMMON_MAX_Y;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_COMMON_SIZE;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_COMMON_WEIGHT;

    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_MID_MIN_Y;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_MID_MAX_Y;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_MID_SIZE;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_MID_WEIGHT;

    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_DEEP_MIN_Y;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_DEEP_MAX_Y;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_DEEP_SIZE;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_DEEP_WEIGHT;

    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_BOTTOM_MIN_Y;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_BOTTOM_MAX_Y;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_BOTTOM_SIZE;
    private static final ForgeConfigSpec.ConfigValue<Integer> TIER_BOTTOM_WEIGHT;

    // ---- Pregen (Stage 7) ----
    private static final ForgeConfigSpec.ConfigValue<Integer> PREGEN_CHUNKS_PER_TICK;

    // ---- Dig limit ----
    private static final ForgeConfigSpec.ConfigValue<Boolean> DIG_LIMIT_ENABLED;
    private static final ForgeConfigSpec.ConfigValue<Integer> DIG_LIMIT_MAX_Y;

    // ---- Mob ore drops (Stage 10) ----
    private static final ForgeConfigSpec.ConfigValue<Boolean> MOB_ORE_DROP_ENABLED;
    private static final ForgeConfigSpec.ConfigValue<Double> MOB_ORE_DROP_CHANCE;

    static {
        BUILDER.comment("Dynamic modded ores (Stage 5): ores contributed by other mods",
                "are auto-scanned and placed in the Mining World by depth tier.")
                .push("modded_ores");

        MODDED_ORE_VEINS_PER_CHUNK = BUILDER
                .comment("How many ore veins are placed per chunk (global frequency).",
                        "Default 6 roughly matches the previous 4..8 random attempts.")
                .defineInRange("veins_per_chunk", 6, 1, 64);
        MODDED_ORE_SIZE_MULTIPLIER = BUILDER
                .comment("Multiplies the size of every modded ore vein.",
                        "1.0 = tier default; 2.0 = twice as big.")
                .defineInRange("size_multiplier", 1.0D, 0.5D, 5.0D);

        BUILDER.comment("COMMON tier: coal-like ores, plentiful, all heights.")
                .push("tier_common");
        TIER_COMMON_MIN_Y = BUILDER.defineInRange("min_y", 0, -64, 320);
        TIER_COMMON_MAX_Y = BUILDER.defineInRange("max_y", 320, -64, 320);
        TIER_COMMON_SIZE = BUILDER.defineInRange("size", 16, 1, 64);
        TIER_COMMON_WEIGHT = BUILDER.defineInRange("weight", 3, 1, 100);
        BUILDER.pop();

        BUILDER.comment("MID tier: iron/copper/tin-like ores.")
                .push("tier_mid");
        TIER_MID_MIN_Y = BUILDER.defineInRange("min_y", 0, -64, 320);
        TIER_MID_MAX_Y = BUILDER.defineInRange("max_y", 256, -64, 320);
        TIER_MID_SIZE = BUILDER.defineInRange("size", 14, 1, 64);
        TIER_MID_WEIGHT = BUILDER.defineInRange("weight", 2, 1, 100);
        BUILDER.pop();

        BUILDER.comment("DEEP tier: gold/redstone/uranium-like ores, lower half.")
                .push("tier_deep");
        TIER_DEEP_MIN_Y = BUILDER.defineInRange("min_y", 0, -64, 320);
        TIER_DEEP_MAX_Y = BUILDER.defineInRange("max_y", 192, -64, 320);
        TIER_DEEP_SIZE = BUILDER.defineInRange("size", 12, 1, 64);
        TIER_DEEP_WEIGHT = BUILDER.defineInRange("weight", 2, 1, 100);
        BUILDER.pop();

        BUILDER.comment("BOTTOM tier: diamond/emerald/ruby-like ores, only deep.")
                .push("tier_bottom");
        TIER_BOTTOM_MIN_Y = BUILDER.defineInRange("min_y", 0, -64, 320);
        TIER_BOTTOM_MAX_Y = BUILDER.defineInRange("max_y", 128, -64, 320);
        TIER_BOTTOM_SIZE = BUILDER.defineInRange("size", 10, 1, 64);
        TIER_BOTTOM_WEIGHT = BUILDER.defineInRange("weight", 2, 1, 100);
        BUILDER.pop();

        BUILDER.pop();

        BUILDER.comment("Chunk pre-generation (Stage 7): /moggminingworld pregen")
                .push("pregen");
        PREGEN_CHUNKS_PER_TICK = BUILDER
                .comment("How many chunks are generated per server tick during pregen.",
                        "Higher = faster but more lag while running.")
                .defineInRange("chunks_per_tick", 8, 1, 256);
        BUILDER.pop();

        BUILDER.comment("Dig limit: 'limit digging up' rule for the Mining World.",
                "When enabled, players cannot break blocks above max_y in the Mining World.")
                .push("dig_limit");
        DIG_LIMIT_ENABLED = BUILDER.define("enabled", false);
        DIG_LIMIT_MAX_Y = BUILDER.defineInRange("max_y", 319, -64, 319);
        BUILDER.pop();

        BUILDER.comment("Mob ore drops (Stage 10): when a mob is killed in the",
                "Mining World it may drop a random ore in addition to its normal loot.",
                "The weighted ore pool is defined in the loot modifier datapack",
                "data/moggminingworld/loot_modifiers/ore_drops.json.")
                .push("mob_ore_drops");
        MOB_ORE_DROP_ENABLED = BUILDER.define("enabled", true);
        MOB_ORE_DROP_CHANCE = BUILDER.comment("Base chance (0..1) per killed mob.")
                .defineInRange("chance", 0.35D, 0.0D, 1.0D);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private MiningWorldConfig() {
    }

    public static int moddedOreVeinsPerChunk() {
        return MODDED_ORE_VEINS_PER_CHUNK.get();
    }

    public static double moddedOreSizeMultiplier() {
        return MODDED_ORE_SIZE_MULTIPLIER.get();
    }

    public static int tierMinY(int tierIndex) {
        return switch (tierIndex) {
            case 1 -> TIER_MID_MIN_Y.get();
            case 2 -> TIER_DEEP_MIN_Y.get();
            case 3 -> TIER_BOTTOM_MIN_Y.get();
            default -> TIER_COMMON_MIN_Y.get();
        };
    }

    public static int tierMaxY(int tierIndex) {
        return switch (tierIndex) {
            case 1 -> TIER_MID_MAX_Y.get();
            case 2 -> TIER_DEEP_MAX_Y.get();
            case 3 -> TIER_BOTTOM_MAX_Y.get();
            default -> TIER_COMMON_MAX_Y.get();
        };
    }

    public static int tierSize(int tierIndex) {
        return switch (tierIndex) {
            case 1 -> TIER_MID_SIZE.get();
            case 2 -> TIER_DEEP_SIZE.get();
            case 3 -> TIER_BOTTOM_SIZE.get();
            default -> TIER_COMMON_SIZE.get();
        };
    }

    public static int tierWeight(int tierIndex) {
        return switch (tierIndex) {
            case 1 -> TIER_MID_WEIGHT.get();
            case 2 -> TIER_DEEP_WEIGHT.get();
            case 3 -> TIER_BOTTOM_WEIGHT.get();
            default -> TIER_COMMON_WEIGHT.get();
        };
    }

    public static int pregenChunksPerTick() {
        return PREGEN_CHUNKS_PER_TICK.get();
    }

    public static boolean digLimitEnabled() {
        return DIG_LIMIT_ENABLED.get();
    }

    public static int digLimitMaxY() {
        return DIG_LIMIT_MAX_Y.get();
    }

    public static boolean mobOreDropEnabled() {
        return MOB_ORE_DROP_ENABLED.get();
    }

    public static double mobOreDropChance() {
        return MOB_ORE_DROP_CHANCE.get();
    }
}