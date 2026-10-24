package com.laodafeijibei.zhongyanfumotai.enchant;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HighLevelEnchantmentsTest {
    private static final ResourceLocation TEST_ID = ResourceLocation.fromNamespaceAndPath("test", "sample");

    @SuppressWarnings("unchecked")
    private static final Holder<Enchantment> TEST_ENCHANTMENT =
            (Holder<Enchantment>) Proxy.newProxyInstance(
                    HighLevelEnchantmentsTest.class.getClassLoader(),
                    new Class<?>[]{Holder.class},
                    (proxy, method, arguments) -> switch (method.getName()) {
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> proxy == arguments[0];
                        default -> throw new UnsupportedOperationException(method.getName());
                    });

    @Test
    void canDowngradeAnExistingEnchantment() {
        Map<Holder<Enchantment>, Integer> merged = HighLevelEnchantments.merge(
                Map.of(TEST_ENCHANTMENT, 7),
                Map.of(TEST_ID, 2),
                Map.of(TEST_ID, TEST_ENCHANTMENT));

        assertEquals(Map.of(TEST_ENCHANTMENT, 2), merged);
    }

    @Test
    void zeroTargetRemovesAnExistingEnchantment() {
        Map<Holder<Enchantment>, Integer> merged = HighLevelEnchantments.merge(
                Map.of(TEST_ENCHANTMENT, 7),
                Map.of(TEST_ID, 0),
                Map.of(TEST_ID, TEST_ENCHANTMENT));

        assertEquals(Map.of(), merged);
    }
}
