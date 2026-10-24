package com.laodafeijibei.zhongyanfumotai.network;

import com.laodafeijibei.zhongyanfumotai.enchant.ExperienceCost;
import com.laodafeijibei.zhongyanfumotai.enchant.HighLevelEnchantments;
import com.laodafeijibei.zhongyanfumotai.menu.EndEnchantingMenu;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public record ApplyEnchantmentsPacket(int containerId, Map<ResourceLocation, Integer> enchantments) {
    private static final int MAX_PACKET_ENCHANTMENTS = 4096;

    public ApplyEnchantmentsPacket {
        enchantments = Map.copyOf(enchantments);
    }

    public static void encode(ApplyEnchantmentsPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.containerId);
        buffer.writeVarInt(packet.enchantments.size());
        packet.enchantments.forEach((id, level) -> {
            buffer.writeResourceLocation(id);
            buffer.writeInt(level);
        });
    }

    public static ApplyEnchantmentsPacket decode(FriendlyByteBuf buffer) {
        int containerId = buffer.readVarInt();
        int size = buffer.readVarInt();
        if (size < 0 || size > MAX_PACKET_ENCHANTMENTS) {
            throw new IllegalArgumentException("Invalid enchantment count: " + size);
        }
        Map<ResourceLocation, Integer> enchantments = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation id = buffer.readResourceLocation();
            int level = buffer.readInt();
            if (enchantments.put(id, level) != null) {
                throw new IllegalArgumentException("Duplicate enchantment: " + id);
            }
        }
        return new ApplyEnchantmentsPacket(containerId, enchantments);
    }

    public static void handle(ApplyEnchantmentsPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> applyOnServer(packet, context.getSender()));
        context.setPacketHandled(true);
    }

    private static void applyOnServer(ApplyEnchantmentsPacket packet, ServerPlayer player) {
        if (player == null || !(player.containerMenu instanceof EndEnchantingMenu menu)
                || menu.containerId != packet.containerId) {
            return;
        }
        if (packet.enchantments.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.end_enchanting_table.no_enchantments"), true);
            return;
        }

        ItemStack input = menu.getTargetStack();
        if (input.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.end_enchanting_table.no_item"), true);
            return;
        }

        Map<ResourceLocation, Enchantment> resolved = new LinkedHashMap<>();
        Map<Enchantment, Integer> targetLevels = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, Integer> entry : packet.enchantments.entrySet()) {
            int level = entry.getValue();
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(entry.getKey());
            if (level < 0 || enchantment == null) {
                player.displayClientMessage(Component.translatable("message.end_enchanting_table.invalid_request"), true);
                return;
            }
            resolved.put(entry.getKey(), enchantment);
            targetLevels.put(enchantment, level);
        }

        Map<Enchantment, Integer> currentLevels = EnchantmentHelper.getEnchantments(input);
        boolean hasChanges = targetLevels.entrySet().stream()
                .anyMatch(entry -> Math.max(0, currentLevels.getOrDefault(entry.getKey(), 0)) != entry.getValue());
        if (!hasChanges) {
            player.displayClientMessage(Component.translatable("message.end_enchanting_table.no_changes"), true);
            return;
        }

        long settlement = ExperienceCost.calculate(currentLevels, targetLevels);
        if (!player.getAbilities().instabuild && settlement > player.experienceLevel) {
            player.displayClientMessage(Component.translatable("message.end_enchanting_table.not_enough_levels", settlement), true);
            return;
        }

        ItemStack output = HighLevelEnchantments.apply(input, packet.enchantments, resolved);
        menu.setTargetStack(output);

        int refundedLevels = 0;
        if (player.getAbilities().instabuild) {
            player.onEnchantmentPerformed(output, 0);
        } else if (settlement > 0L) {
            player.onEnchantmentPerformed(output, (int) settlement);
        } else {
            player.onEnchantmentPerformed(output, 0);
            if (settlement < 0L) {
                long refundCapacity = (long) Integer.MAX_VALUE - player.experienceLevel;
                refundedLevels = (int) Math.min(-settlement, refundCapacity);
                if (refundedLevels > 0) {
                    player.giveExperienceLevels(refundedLevels);
                }
            }
        }

        int criterionLevel = (int) Math.min(Math.max(settlement, 0L), Integer.MAX_VALUE);
        CriteriaTriggers.ENCHANTED_ITEM.trigger(player, output, criterionLevel);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.BLOCKS, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
        if (player.getAbilities().instabuild) {
            player.displayClientMessage(Component.translatable("message.end_enchanting_table.success_creative"), true);
        } else if (settlement > 0L) {
            player.displayClientMessage(Component.translatable("message.end_enchanting_table.success_cost", settlement), true);
        } else if (settlement < 0L) {
            player.displayClientMessage(Component.translatable("message.end_enchanting_table.success_refund", refundedLevels), true);
        } else {
            player.displayClientMessage(Component.translatable("message.end_enchanting_table.success_balanced"), true);
        }
    }
}
