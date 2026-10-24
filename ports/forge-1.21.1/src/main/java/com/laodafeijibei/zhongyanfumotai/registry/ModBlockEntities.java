package com.laodafeijibei.zhongyanfumotai.registry;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import com.laodafeijibei.zhongyanfumotai.block.entity.EndEnchantingTableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ZhongyanEnchantingTableMod.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> LEGACY_BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ZhongyanEnchantingTableMod.LEGACY_MOD_ID);

    public static final RegistryObject<BlockEntityType<EndEnchantingTableBlockEntity>> END_ENCHANTING_TABLE =
            BLOCK_ENTITY_TYPES.register("end_enchanting_table",
                    () -> BlockEntityType.Builder.of(EndEnchantingTableBlockEntity::new,
                            ModBlocks.END_ENCHANTING_TABLE.get()).build(null));

    private static final RegistryObject<BlockEntityType<EndEnchantingTableBlockEntity>> LEGACY_END_ENCHANTING_TABLE =
            LEGACY_BLOCK_ENTITY_TYPES.register("end_enchanting_table",
                    () -> BlockEntityType.Builder.of(EndEnchantingTableBlockEntity::new,
                            ModBlocks.END_ENCHANTING_TABLE.get()).build(null));

    private ModBlockEntities() {}

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
        LEGACY_BLOCK_ENTITY_TYPES.register(modBus);
    }
}
