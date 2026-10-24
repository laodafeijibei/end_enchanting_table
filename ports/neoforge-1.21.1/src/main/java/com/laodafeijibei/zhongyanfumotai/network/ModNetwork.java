package com.laodafeijibei.zhongyanfumotai.network;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModNetwork {
    private ModNetwork() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(
                ApplyEnchantmentsPacket.TYPE,
                ApplyEnchantmentsPacket.STREAM_CODEC,
                ApplyEnchantmentsPacket::handle);
    }

    public static void sendToServer(ApplyEnchantmentsPacket packet) {
        PacketDistributor.sendToServer(packet);
    }
}
