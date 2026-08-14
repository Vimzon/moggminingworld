package com.mogg.miningworld.worldgen;

import com.mogg.miningworld.MoggMiningWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Stage 5: dynamically scans the block registry for ores contributed by other
 * mods (forge:ores tag, fallback: block id contains "_ore") and places them in
 * the Mining World, distributed by depth like in the Overworld.
 */
public class DynamicOreFeature extends Feature<DynamicOreConfig> {

    private static final Logger LOGGER = LogManager.getLogger();

    private List<OreEntry> cachedOres;
    private int placementLogCounter = 0;

    public DynamicOreFeature() {
        super(DynamicOreConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<DynamicOreConfig> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        List<OreEntry> ores = getOres(level);
        if (ores.isEmpty()) {
            return false;
        }
        OreEntry entry = pickWeighted(ores, random);
        int x = context.origin().getX();
        int z = context.origin().getZ();
        int y = Mth.randomBetweenInclusive(random, entry.minY(), entry.maxY());
        boolean placed = placeVein(level, random, new BlockPos(x, y, z), entry);
        if (placed && (placementLogCounter++ % 100 == 0)) {
            LOGGER.info("[Mogg] Dynamic ore vein placed: {} at ({}, {}, {}), tier y{}..{}",
                    entry.stone().getBlock().getDescriptionId(), x, y, z, entry.minY(), entry.maxY());
        }
        return placed;
    }

    private List<OreEntry> getOres(WorldGenLevel level) {
        if (cachedOres != null) {
            return cachedOres;
        }
        List<OreEntry> result = new ArrayList<>();
        try {
            Registry<Block> blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);
            Map<String, Block> stoneByName = new HashMap<>();
            Map<String, Block> deepslateByName = new HashMap<>();

            for (Block block : ForgeRegistries.BLOCKS.getValues()) {
                ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
                if (id == null || "minecraft".equals(id.getNamespace())) {
                    continue;
                }
                String path = id.getPath();
                if (!isOre(block, id, blockRegistry)) {
                    continue;
                }
                if (path.startsWith("deepslate_")) {
                    deepslateByName.put(path.substring("deepslate_".length()), block);
                } else {
                    stoneByName.put(path, block);
                }
            }

            Set<String> names = new LinkedHashSet<>();
            names.addAll(stoneByName.keySet());
            names.addAll(deepslateByName.keySet());

            for (String name : names) {
                Block stone = stoneByName.get(name);
                Block deepslate = deepslateByName.get(name);
                if (stone == null) {
                    stone = deepslate;
                }
                if (deepslate == null) {
                    deepslate = stone;
                }
                OreTier tier = tierFor(name);
                result.add(new OreEntry(
                        stone.defaultBlockState(),
                        deepslate.defaultBlockState(),
                        tier.minY, tier.maxY, tier.size, tier.weight));
            }
            LOGGER.info("[Mogg] Dynamic ore scan found {} modded ore(s): {}", result.size(),
                    result.stream().map(e -> e.stone().getBlock().getDescriptionId()).toList());
        } catch (Exception e) {
            LOGGER.error("[Mogg] Failed to scan modded ores, skipping Stage 5 generation", e);
        }
        cachedOres = result;
        return result;
    }

    private boolean isOre(Block block, ResourceLocation id, Registry<Block> blockRegistry) {
        String path = id.getPath();
        if (path.endsWith("_ore")) {
            return true;
        }
        try {
            ResourceKey<Block> key = blockRegistry.getResourceKey(block).orElse(null);
            if (key != null) {
                return blockRegistry.getHolder(key).map(holder -> holder.is(Tags.Blocks.ORES)).orElse(false);
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private OreTier tierFor(String name) {
        String p = name.toLowerCase(Locale.ROOT);
        if (p.contains("coal")) {
            return OreTier.COMMON;
        }
        if (p.contains("diamond") || p.contains("emerald") || p.contains("ruby")
                || p.contains("sapphire") || p.contains("topaz") || p.contains("peridot")
                || p.contains("tanzanite") || p.contains("amber")) {
            return OreTier.BOTTOM;
        }
        if (p.contains("gold") || p.contains("redstone") || p.contains("lapis")
                || p.contains("uranium") || p.contains("platinum") || p.contains("osmium")) {
            return OreTier.DEEP;
        }
        if (p.contains("iron") || p.contains("copper") || p.contains("tin") || p.contains("lead")
                || p.contains("silver") || p.contains("nickel") || p.contains("zinc")
                || p.contains("aluminum") || p.contains("aluminium") || p.contains("bauxite")) {
            return OreTier.MID;
        }
        return OreTier.MID;
    }

    private OreEntry pickWeighted(List<OreEntry> entries, RandomSource random) {
        int total = 0;
        for (OreEntry entry : entries) {
            total += entry.weight();
        }
        if (total <= 0) {
            return entries.get(0);
        }
        int roll = random.nextInt(total);
        for (OreEntry entry : entries) {
            roll -= entry.weight();
            if (roll < 0) {
                return entry;
            }
        }
        return entries.get(entries.size() - 1);
    }

    private boolean placeVein(WorldGenLevel level, RandomSource random, BlockPos pos, OreEntry entry) {
        float f = random.nextFloat() * (float) Math.PI;
        float f1 = (float) entry.size() / 8.0F;
        int i = Mth.floor(((double) entry.size() / 16.0D * 2.0D + 1.0D) / 2.0D);

        double d0 = pos.getX() + Math.sin(f) * f1;
        double d1 = pos.getX() - Math.sin(f) * f1;
        double d2 = pos.getZ() + Math.cos(f) * f1;
        double d3 = pos.getZ() - Math.cos(f) * f1;
        double d4 = pos.getY() + random.nextInt(3) - 2;
        double d5 = pos.getY() + random.nextInt(3) - 2;

        int k = pos.getX() - Mth.ceil(f1) - i;
        int l = pos.getY() - 2 - i;
        int i1 = pos.getZ() - Mth.ceil(f1) - i;
        int j1 = 2 * (Mth.ceil(f1) + i);
        int k1 = 2 * (2 + i);

        for (int l1 = k; l1 <= k + j1; ++l1) {
            for (int i2 = l; i2 <= l + k1; ++i2) {
                for (int j2 = i1; j2 <= i1 + j1; ++j2) {
                    BlockPos blockpos = new BlockPos(l1, i2, j2);
                    double d6 = Mth.length((double) (l1 + 0.5D - d0), (double) (i2 - d4), (double) (j2 + 0.5D - d2));
                    if (d6 <= f1 * f1 + 1.0D) {
                        double d7 = Mth.length((double) (l1 + 0.5D - d1), (double) (i2 - d5), (double) (j2 + 0.5D - d3));
                        if (d7 <= f1 * f1 + 1.0D) {
                            BlockState state = level.getBlockState(blockpos);
                            if (state.is(Blocks.STONE)) {
                                level.setBlock(blockpos, entry.stone(), 2);
                            } else if (state.is(Blocks.DEEPSLATE)) {
                                level.setBlock(blockpos, entry.deepslate(), 2);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private record OreEntry(BlockState stone, BlockState deepslate, int minY, int maxY, int size, int weight) {
    }

    private enum OreTier {
        COMMON(0, 320, 14, 3),
        MID(0, 256, 12, 2),
        DEEP(0, 192, 10, 2),
        BOTTOM(0, 128, 8, 2);

        final int minY;
        final int maxY;
        final int size;
        final int weight;

        OreTier(int minY, int maxY, int size, int weight) {
            this.minY = minY;
            this.maxY = maxY;
            this.size = size;
            this.weight = weight;
        }
    }
}