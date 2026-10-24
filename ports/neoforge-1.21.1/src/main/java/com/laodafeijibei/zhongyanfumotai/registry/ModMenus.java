package com.laodafeijibei.zhongyanfumotai.registry;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import com.laodafeijibei.zhongyanfumotai.menu.EndEnchantingMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, ZhongyanEnchantingTableMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<EndEnchantingMenu>> END_ENCHANTING_TABLE =
            MENUS.register("end_enchanting_table", () -> IMenuTypeExtension.create(EndEnchantingMenu::new));

    private ModMenus() {}

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }
}
