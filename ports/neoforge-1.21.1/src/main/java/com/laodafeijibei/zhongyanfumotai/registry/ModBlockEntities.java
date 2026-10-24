package com.laodafeijibei.zhongyanfumotai.registry;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import com.laodafeijibei.zhongyanfumotai.block.entity.EndEnchantingTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ZhongyanEnchantingTableMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EndEnchantingTableBlockEntity>> END_ENCHANTING_TABLE =
            BLOCK_ENTITY_TYPES.register("end_enchanting_table",
                    () -> BlockEntityType.Builder.of(EndEnchantingTableBlockEntity::new,
                            ModBlocks.END_ENCHANTING_TABLE.get()).build(null));

    static {
        BLOCK_ENTITY_TYPES.addAlias(
                ResourceLocation.fromNamespaceAndPath(ZhongyanEnchantingTableMod.LEGACY_MOD_ID, "end_enchanting_table"),
                ResourceLocation.fromNamespaceAndPath(ZhongyanEnchantingTableMod.MOD_ID, "end_enchanting_table"));
    }

    private ModBlockEntities() {}

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}
