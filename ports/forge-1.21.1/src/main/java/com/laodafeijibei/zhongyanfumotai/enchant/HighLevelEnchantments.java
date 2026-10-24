package com.laodafeijibei.zhongyanfumotai.enchant;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HighLevelEnchantments {
    private HighLevelEnchantments() {}

    public static ItemStack apply(ItemStack input, Map<ResourceLocation, Integer> requested,
                                  Map<ResourceLocation, Holder<Enchantment>> resolved) {
        boolean book = input.is(Items.BOOK) || input.is(Items.ENCHANTED_BOOK);
        ItemStack output = input.is(Items.BOOK) ? new ItemStack(Items.ENCHANTED_BOOK) : input.copy();
        output.setCount(1);

        net.minecraft.core.component.DataComponentType<ItemEnchantments> component =
                book ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
        ItemEnchantments current = output.getOrDefault(component, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(current);
        for (Map.Entry<ResourceLocation, Integer> entry : requested.entrySet()) {
            Holder<Enchantment> enchantment = resolved.get(entry.getKey());
            if (enchantment != null) {
                mutable.set(enchantment, entry.getValue());
            }
        }
        ItemEnchantments result = mutable.toImmutable();
        if (result.isEmpty()) {
            output.remove(component);
        } else {
            output.set(component, result);
        }
        return output;
    }

    public static Map<Holder<Enchantment>, Integer> current(ItemStack stack) {
        return EnchantmentsAccess.levels(stack);
    }

    static Map<Holder<Enchantment>, Integer> merge(Map<Holder<Enchantment>, Integer> current,
                                                    Map<ResourceLocation, Integer> requested,
                                                    Map<ResourceLocation, Holder<Enchantment>> resolved) {
        Map<Holder<Enchantment>, Integer> merged = new LinkedHashMap<>(current);
        for (Map.Entry<ResourceLocation, Integer> entry : requested.entrySet()) {
            Holder<Enchantment> enchantment = resolved.get(entry.getKey());
            if (enchantment != null) {
                if (entry.getValue() == 0) {
                    merged.remove(enchantment);
                } else {
                    merged.put(enchantment, entry.getValue());
                }
            }
        }
        return merged;
    }
}
