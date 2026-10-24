package com.laodafeijibei.zhongyanfumotai.network;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(ZhongyanEnchantingTableMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static boolean registered;

    private ModNetwork() {}

    public static synchronized void register() {
        if (registered) {
            return;
        }
        int messageId = 0;
        CHANNEL.registerMessage(messageId, ApplyEnchantmentsPacket.class,
                ApplyEnchantmentsPacket::encode,
                ApplyEnchantmentsPacket::decode,
                ApplyEnchantmentsPacket::handle);
        registered = true;
    }

    public static void sendToServer(ApplyEnchantmentsPacket packet) {
        CHANNEL.sendToServer(packet);
    }
}
