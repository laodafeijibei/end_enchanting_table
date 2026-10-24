package com.laodafeijibei.zhongyanfumotai.enchant;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HighLevelEnchantments {
    private HighLevelEnchantments() {}

    public static ItemStack apply(ItemStack input, Map<ResourceLocation, Integer> requested,
                                  Map<ResourceLocation, Enchantment> resolved) {
        Map<Enchantment, Integer> merged = merge(
                EnchantmentHelper.getEnchantments(input), requested, resolved);
        boolean book = input.is(Items.BOOK) || input.is(Items.ENCHANTED_BOOK);
        ItemStack output;
        if (book) {
            output = new ItemStack(merged.isEmpty() ? Items.BOOK : Items.ENCHANTED_BOOK);
            if (input.hasTag()) {
                output.setTag(input.getTag().copy());
            }
        } else {
            output = input.copy();
        }
        output.setCount(1);

        // Vanilla only raises stored-book levels, so rebuild this list before a downgrade.
        if (book) {
            output.removeTagKey("StoredEnchantments");
        }
        EnchantmentHelper.setEnchantments(merged, output);
        return output;
    }

    static Map<Enchantment, Integer> merge(Map<Enchantment, Integer> current,
                                           Map<ResourceLocation, Integer> requested,
                                           Map<ResourceLocation, Enchantment> resolved) {
        Map<Enchantment, Integer> merged = new LinkedHashMap<>(current);
        for (Map.Entry<ResourceLocation, Integer> entry : requested.entrySet()) {
            Enchantment enchantment = resolved.get(entry.getKey());
            if (entry.getValue() == 0) {
                merged.remove(enchantment);
            } else {
                merged.put(enchantment, entry.getValue());
            }
        }
        return merged;
    }
}
