package com.laodafeijibei.zhongyanfumotai.registry;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import com.laodafeijibei.zhongyanfumotai.block.EndEnchantingTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ZhongyanEnchantingTableMod.MOD_ID);

    public static final RegistryObject<Block> END_ENCHANTING_TABLE = BLOCKS.register(
            "end_enchanting_table",
            () -> new EndEnchantingTableBlock(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE)
                    .lightLevel(state -> 7))
    );

    private ModBlocks() {}

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
