package com.laodafeijibei.zhongyanfumotai;

import com.laodafeijibei.zhongyanfumotai.network.ModNetwork;
import com.laodafeijibei.zhongyanfumotai.registry.ModBlockEntities;
import com.laodafeijibei.zhongyanfumotai.registry.ModBlocks;
import com.laodafeijibei.zhongyanfumotai.registry.ModItems;
import com.laodafeijibei.zhongyanfumotai.registry.ModMenus;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(ZhongyanEnchantingTableMod.MOD_ID)
public final class ZhongyanEnchantingTableMod {
    public static final String MOD_ID = "end_enchanting_table";
    public static final String LEGACY_MOD_ID = "zhongyanfumotai";

    public ZhongyanEnchantingTableMod(IEventBus modBus) {
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        modBus.addListener(this::addToCreativeTabs);
        modBus.addListener(this::registerCapabilities);
        modBus.addListener(ModNetwork::register);
    }

    private void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.END_ENCHANTING_TABLE.get());
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.END_ENCHANTING_TABLE.get(),
                (table, side) -> table.getInventory());
    }
}
