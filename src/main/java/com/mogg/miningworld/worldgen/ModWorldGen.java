package com.mogg.miningworld.worldgen;

import com.mogg.miningworld.MoggMiningWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModWorldGen {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, MoggMiningWorld.MOD_ID);

    public static final RegistryObject<Feature<?>> DYNAMIC_ORE =
            FEATURES.register("dynamic_ore", DynamicOreFeature::new);
}