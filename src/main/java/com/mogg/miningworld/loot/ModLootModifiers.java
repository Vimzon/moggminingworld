package com.mogg.miningworld.loot;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.mogg.miningworld.MoggMiningWorld.MOD_ID;

/**
 * Stage 10: registers global loot modifier serializers.
 *
 * The actual loot modifier instance is loaded from the datapack
 * data/moggminingworld/loot_modifiers/ore_drops.json and registered for the
 * whole game in data/moggminingworld/forge/loot_modifiers/global_loot_modifiers.json.
 */
public class ModLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLM =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    public static void register() {
        GLM.register("ore_drops", OreDropModifier.CODEC);
    }
}