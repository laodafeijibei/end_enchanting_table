package com.laodafeijibei.zhongyanfumotai.registry;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import com.laodafeijibei.zhongyanfumotai.block.EndEnchantingTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, ZhongyanEnchantingTableMod.MOD_ID);

    public static final DeferredHolder<Block, Block> END_ENCHANTING_TABLE = BLOCKS.register(
            "end_enchanting_table",
            () -> new EndEnchantingTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE)
                    .lightLevel(state -> 7))
    );

    static {
        BLOCKS.addAlias(
                ResourceLocation.fromNamespaceAndPath(ZhongyanEnchantingTableMod.LEGACY_MOD_ID, "end_enchanting_table"),
                ResourceLocation.fromNamespaceAndPath(ZhongyanEnchantingTableMod.MOD_ID, "end_enchanting_table"));
    }

    private ModBlocks() {}

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
