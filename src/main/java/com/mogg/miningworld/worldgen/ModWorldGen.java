package com.mogg.miningworld.worldgen;

import com.mogg.miningworld.MoggMiningWorld;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModWorldGen {

    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registries.CONFIGURED_FEATURE, MoggMiningWorld.MOD_ID);

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registries.PLACED_FEATURE, MoggMiningWorld.MOD_ID);

    public static final RegistryObject<ConfiguredFeature<?, ?>> DYNAMIC_ORES =
            CONFIGURED_FEATURES.register("dynamic_modded_ores",
                    () -> new ConfiguredFeature<>(new DynamicOreFeature(), new DynamicOreConfig(0, 120)));

    public static final RegistryObject<PlacedFeature> DYNAMIC_ORES_PLACED =
            PLACED_FEATURES.register("dynamic_modded_ores",
                    () -> new PlacedFeature(
                            Holder.direct(DYNAMIC_ORES.get()),
                            List.of(
                                    RarityFilter.onAverageOnceEvery(24),
                                    InSquarePlacement.spread(),
                                    HeightRangePlacement.uniform(
                                            VerticalAnchor.absolute(0),
                                            VerticalAnchor.absolute(120)),
                                    BiomeFilter.biome())));
}