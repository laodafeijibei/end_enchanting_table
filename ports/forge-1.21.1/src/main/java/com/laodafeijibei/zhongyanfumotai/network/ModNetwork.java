package com.laodafeijibei.zhongyanfumotai.network;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class ModNetwork {
    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(ZhongyanEnchantingTableMod.MOD_ID, "main"))
            .networkProtocolVersion(1)
            .simpleChannel();
    private static boolean registered;

    private ModNetwork() {}

    public static synchronized void register() {
        if (registered) {
            return;
        }
        CHANNEL.messageBuilder(ApplyEnchantmentsPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ApplyEnchantmentsPacket::encode)
                .decoder(ApplyEnchantmentsPacket::decode)
                .consumerMainThread(ApplyEnchantmentsPacket::handle)
                .add();
        registered = true;
    }

    public static void sendToServer(ApplyEnchantmentsPacket packet) {
        CHANNEL.send(packet, PacketDistributor.SERVER.noArg());
    }
}
