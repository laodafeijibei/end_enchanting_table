package com.laodafeijibei.zhongyanfumotai.registry;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ZhongyanEnchantingTableMod.MOD_ID);

    public static final RegistryObject<Item> END_ENCHANTING_TABLE = ITEMS.register(
            "end_enchanting_table",
            () -> new BlockItem(ModBlocks.END_ENCHANTING_TABLE.get(),
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant())
    );

    private ModItems() {}

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
