package com.laodafeijibei.zhongyanfumotai.registry;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, ZhongyanEnchantingTableMod.MOD_ID);

    public static final DeferredHolder<Item, Item> END_ENCHANTING_TABLE = ITEMS.register(
            "end_enchanting_table",
            () -> new BlockItem(ModBlocks.END_ENCHANTING_TABLE.get(),
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant())
    );

    static {
        ITEMS.addAlias(
                ResourceLocation.fromNamespaceAndPath(ZhongyanEnchantingTableMod.LEGACY_MOD_ID, "end_enchanting_table"),
                ResourceLocation.fromNamespaceAndPath(ZhongyanEnchantingTableMod.MOD_ID, "end_enchanting_table"));
    }

    private ModItems() {}

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
