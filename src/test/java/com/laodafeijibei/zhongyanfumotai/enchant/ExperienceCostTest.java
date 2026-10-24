package com.laodafeijibei.zhongyanfumotai.enchant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExperienceCostTest {
    @Test
    void supportsIntegerMaxLevelWithoutOverflow() {
        assertEquals(2L * Integer.MAX_VALUE,
                ExperienceCost.addDeltaSaturated(0L, 0, Integer.MAX_VALUE, ExperienceCost.NORMAL_MULTIPLIER));
        assertEquals(5L * Integer.MAX_VALUE,
                ExperienceCost.addDeltaSaturated(0L, 0, Integer.MAX_VALUE, ExperienceCost.TREASURE_MULTIPLIER));
    }

    @Test
    void settlesUpgradesAndDowngradesTogether() {
        long settlement = ExperienceCost.addDeltaSaturated(0L, 2, 12, ExperienceCost.NORMAL_MULTIPLIER);
        settlement = ExperienceCost.addDeltaSaturated(settlement, 7, 3, ExperienceCost.TREASURE_MULTIPLIER);
        settlement = ExperienceCost.addDeltaSaturated(settlement, 1, 4, ExperienceCost.CURSE_MULTIPLIER);
        assertEquals(3L, settlement);
    }

    @Test
    void removingAnEnchantmentProducesARefund() {
        assertEquals(-35L,
                ExperienceCost.addDeltaSaturated(0L, 7, 0, ExperienceCost.TREASURE_MULTIPLIER));
    }

    @Test
    void equalLevelsAreANoOp() {
        assertEquals(99L,
                ExperienceCost.addDeltaSaturated(99L, Integer.MAX_VALUE, Integer.MAX_VALUE,
                        ExperienceCost.NORMAL_MULTIPLIER));
    }

    @Test
    void ignoresInvalidInputs() {
        assertEquals(99L, ExperienceCost.addDeltaSaturated(99L, -1, 0, ExperienceCost.NORMAL_MULTIPLIER));
        assertEquals(99L, ExperienceCost.addDeltaSaturated(99L, 0, -1, ExperienceCost.NORMAL_MULTIPLIER));
    }
}
