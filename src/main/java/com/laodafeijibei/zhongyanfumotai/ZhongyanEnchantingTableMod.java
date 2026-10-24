package com.laodafeijibei.zhongyanfumotai;

import com.mojang.logging.LogUtils;
import com.laodafeijibei.zhongyanfumotai.network.ModNetwork;
import com.laodafeijibei.zhongyanfumotai.registry.ModBlockEntities;
import com.laodafeijibei.zhongyanfumotai.registry.ModBlocks;
import com.laodafeijibei.zhongyanfumotai.registry.ModItems;
import com.laodafeijibei.zhongyanfumotai.registry.ModMenus;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.MissingMappingsEvent;
import org.slf4j.Logger;

@Mod(ZhongyanEnchantingTableMod.MOD_ID)
public final class ZhongyanEnchantingTableMod {
    public static final String MOD_ID = "end_enchanting_table";
    public static final String LEGACY_MOD_ID = "zhongyanfumotai";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ZhongyanEnchantingTableMod(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::addToCreativeTabs);
        MinecraftForge.EVENT_BUS.addListener(this::remapLegacyIds);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetwork.register();
            verifyIntEnchantmentLevels();
        });
    }

    private static void verifyIntEnchantmentLevels() {
        ResourceLocation probeId = ResourceLocation.fromNamespaceAndPath(MOD_ID, "runtime_probe");
        CompoundTag probe = EnchantmentHelper.storeEnchantment(probeId, Integer.MAX_VALUE);
        int stored = probe.getInt("lvl");
        int loaded = EnchantmentHelper.getEnchantmentLevel(probe);
        if (stored != Integer.MAX_VALUE || loaded != Integer.MAX_VALUE) {
            throw new IllegalStateException("32-bit enchantment level mixin failed: stored="
                    + stored + ", loaded=" + loaded);
        }
        LOGGER.info("32-bit enchantment level runtime probe passed: {}", loaded);
    }

    private void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.END_ENCHANTING_TABLE.get());
        }
    }

    private void remapLegacyIds(MissingMappingsEvent event) {
        event.getMappings(Registries.BLOCK, LEGACY_MOD_ID).stream()
                .filter(mapping -> mapping.getKey().getPath().equals("end_enchanting_table"))
                .forEach(mapping -> mapping.remap(ModBlocks.END_ENCHANTING_TABLE.get()));
        event.getMappings(Registries.ITEM, LEGACY_MOD_ID).stream()
                .filter(mapping -> mapping.getKey().getPath().equals("end_enchanting_table"))
                .forEach(mapping -> mapping.remap(ModItems.END_ENCHANTING_TABLE.get()));
    }
}
