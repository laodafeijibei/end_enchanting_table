package com.laodafeijibei.zhongyanfumotai.registry;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import com.laodafeijibei.zhongyanfumotai.menu.EndEnchantingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ZhongyanEnchantingTableMod.MOD_ID);

    public static final RegistryObject<MenuType<EndEnchantingMenu>> END_ENCHANTING_TABLE =
            MENUS.register("end_enchanting_table", () -> IForgeMenuType.create(EndEnchantingMenu::new));

    private ModMenus() {}

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }
}
