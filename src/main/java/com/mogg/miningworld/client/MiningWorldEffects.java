package com.mogg.miningworld.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Stage 2, step 2.5: client-side visual effects for Mining World.
 *
 * This is render-only - it does NOT touch world generation. Goal: no vanilla
 * sky, sun, moon, stars, or clouds, since Mining World is a fully enclosed
 * underground dimension (has_ceiling: true, has_skylight: false in the
 * dimension_type). Fog color itself already comes from the biome
 * (fog_color/sky_color/water_color in worldgen/biome/mining_world.json,
 * done in step 2.2) - this class does not override that.
 *
 * Shape copied from vanilla NetherEffects/EndEffects (the two vanilla
 * "no real sky" dimensions), trimmed to just what we need:
 * - cloudLevel = NaN            -> no clouds rendered at all
 * - hasGround = false           -> no flat ground plane rendered below the world
 * - skyType = NONE              -> no sky dome / sun / moon / stars
 * - forceBrightLightmap = false -> normal light-based lightmap (no self-lit look)
 * - constantAmbientLight = false-> normal ambient lighting behaviour
 *
 * Unlike NetherEffects, isFoggyAt() returns false here: we don't want to
 * force extra haze everywhere, just rely on the biome's own fog color.
 */
@OnlyIn(Dist.CLIENT)
public class MiningWorldEffects extends DimensionSpecialEffects {

    public MiningWorldEffects() {
        super(Float.NaN, false, DimensionSpecialEffects.SkyType.NONE, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
        // No sun below ground - don't let fog color shift with sun height.
        return color;
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }
}
