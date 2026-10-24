package com.laodafeijibei.zhongyanfumotai.enchant;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HighLevelEnchantmentsTest {
    private static final ResourceLocation TEST_ID = ResourceLocation.fromNamespaceAndPath("test", "sample");
    private static final Enchantment TEST_ENCHANTMENT = new Enchantment(
            Enchantment.Rarity.COMMON, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}) {};

    @Test
    void canDowngradeAnExistingEnchantment() {
        Map<Enchantment, Integer> merged = HighLevelEnchantments.merge(
                Map.of(TEST_ENCHANTMENT, 7),
                Map.of(TEST_ID, 2),
                Map.of(TEST_ID, TEST_ENCHANTMENT));

        assertEquals(Map.of(TEST_ENCHANTMENT, 2), merged);
    }

    @Test
    void zeroTargetRemovesAnExistingEnchantment() {
        Map<Enchantment, Integer> merged = HighLevelEnchantments.merge(
                Map.of(TEST_ENCHANTMENT, 7),
                Map.of(TEST_ID, 0),
                Map.of(TEST_ID, TEST_ENCHANTMENT));

        assertEquals(Map.of(), merged);
    }
}
