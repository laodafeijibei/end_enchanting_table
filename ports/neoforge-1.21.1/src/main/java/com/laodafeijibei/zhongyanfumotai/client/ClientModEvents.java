package com.laodafeijibei.zhongyanfumotai.client;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import com.laodafeijibei.zhongyanfumotai.client.renderer.EndEnchantingTableRenderer;
import com.laodafeijibei.zhongyanfumotai.client.screen.EndEnchantingScreen;
import com.laodafeijibei.zhongyanfumotai.registry.ModBlockEntities;
import com.laodafeijibei.zhongyanfumotai.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ZhongyanEnchantingTableMod.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.END_ENCHANTING_TABLE.get(), EndEnchantingScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.END_ENCHANTING_TABLE.get(),
                EndEnchantingTableRenderer::new);
    }
}
