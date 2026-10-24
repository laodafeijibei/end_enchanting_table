package com.laodafeijibei.zhongyanfumotai.client;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import com.laodafeijibei.zhongyanfumotai.client.renderer.EndEnchantingTableRenderer;
import com.laodafeijibei.zhongyanfumotai.client.screen.EndEnchantingScreen;
import com.laodafeijibei.zhongyanfumotai.registry.ModBlockEntities;
import com.laodafeijibei.zhongyanfumotai.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ZhongyanEnchantingTableMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenus.END_ENCHANTING_TABLE.get(), EndEnchantingScreen::new));
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.END_ENCHANTING_TABLE.get(),
                EndEnchantingTableRenderer::new);
    }
}
