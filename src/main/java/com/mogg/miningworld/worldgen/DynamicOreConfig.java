package com.mogg.miningworld.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record DynamicOreConfig(int minY, int maxY) implements FeatureConfiguration {
    public static final Codec<DynamicOreConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("min_y").forGetter(DynamicOreConfig::minY),
                    Codec.INT.fieldOf("max_y").forGetter(DynamicOreConfig::maxY)
            ).apply(instance, DynamicOreConfig::new));
}