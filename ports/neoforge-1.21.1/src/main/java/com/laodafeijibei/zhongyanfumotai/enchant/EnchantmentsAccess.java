package com.laodafeijibei.zhongyanfumotai.enchant;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EnchantmentsAccess {
    private EnchantmentsAccess() {}

    public static Registry<Enchantment> registry(RegistryAccess access) {
        return access.registryOrThrow(Registries.ENCHANTMENT);
    }

    public static Holder<Enchantment> resolve(Registry<Enchantment> registry, ResourceLocation id) {
        return registry.getHolder(ResourceKey.create(Registries.ENCHANTMENT, id)).orElse(null);
    }

    public static ResourceLocation id(Holder<Enchantment> holder) {
        return holder.unwrapKey().map(ResourceKey::location)
                .orElseThrow(() -> new IllegalArgumentException("Unregistered enchantment: " + holder));
    }

    public static Map<Holder<Enchantment>, Integer> levels(ItemStack stack) {
        Map<Holder<Enchantment>, Integer> levels = new LinkedHashMap<>();
        ItemEnchantments enchantments = stack.getEnchantments();
        enchantments.entrySet().forEach(entry -> levels.put(entry.getKey(), entry.getIntValue()));
        return levels;
    }
}
