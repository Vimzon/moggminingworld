package com.mogg.miningworld.block;

import com.mogg.miningworld.MoggMiningWorld;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Stage 6: registers the Mining Portal block and its item form.
 *
 * The portal block is intentionally unobtainable through crafting or loot -
 * it is meant to be placed by an admin (e.g. /give) at a fixed spawn point
 * in the Overworld. Entry into Mining World and exit back to Overworld both
 * go through this single block.
 */
public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MoggMiningWorld.MOD_ID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MoggMiningWorld.MOD_ID);

    public static final RegistryObject<MiningPortalBlock> MINING_PORTAL =
            BLOCKS.register("mining_portal", () -> new MiningPortalBlock(
                    BlockBehaviour.Properties.of()
                            .strength(-1.0F, 3600000.0F)
                            .noOcclusion()));

    public static final RegistryObject<BlockItem> MINING_PORTAL_ITEM =
            ITEMS.register("mining_portal", () -> new BlockItem(MINING_PORTAL.get(), new Item.Properties()));
}
