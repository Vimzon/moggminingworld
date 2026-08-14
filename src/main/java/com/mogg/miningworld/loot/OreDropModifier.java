package com.mogg.miningworld.loot;

import com.mogg.miningworld.MoggMiningWorld;
import com.mogg.miningworld.config.MiningWorldConfig;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

/**
 * Stage 10: mobs killed in the Mining World drop a random ore in addition to
 * their normal loot.
 *
 * The weighted ore pool is defined in the datapack
 * data/moggminingworld/loot_modifiers/ore_drops.json (see the "ore_pool"
 * field); the global chance is controlled by the config
 * (mob_ore_drops.chance). Ores are only added in the Mining World dimension,
 * so vanilla mobs in the Overworld are not affected.
 */
public class OreDropModifier extends LootModifier {

    public static final Supplier<Codec<OreDropModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst ->
                    codecStart(inst)
                            .and(Codec.list(Codec.STRING).fieldOf("ore_pool").forGetter(m -> m.orePool))
                            .apply(inst, OreDropModifier::new)));

    private final List<String> orePool;

    public OreDropModifier(LootItemCondition[] conditions, List<String> orePool) {
        super(conditions);
        this.orePool = orePool;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!MiningWorldConfig.mobOreDropEnabled()) {
            return generatedLoot;
        }
        if (context.getLevel() == null
                || context.getLevel().dimension() != MoggMiningWorld.MINING_WORLD_KEY) {
            return generatedLoot;
        }
        if (!(context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Mob)) {
            return generatedLoot;
        }
        if (context.getRandom().nextDouble() > MiningWorldConfig.mobOreDropChance()) {
            return generatedLoot;
        }
        ItemStack ore = pickOre();
        if (!ore.isEmpty()) {
            generatedLoot.add(ore);
        }
        return generatedLoot;
    }

    private ItemStack pickOre() {
        if (orePool.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int index = (int) (Math.random() * orePool.size());
        String id = orePool.get(index);
        try {
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (location != null) {
                Item item = ForgeRegistries.ITEMS.getValue(location);
                if (item != null && item != Items.AIR) {
                    return new ItemStack(item);
                }
            }
        } catch (Exception ignored) {
        }
        return ItemStack.EMPTY;
    }
}