package com.laodafeijibei.zhongyanfumotai.enchant;

import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;

public final class ExperienceCost {
    public static final int CURSE_MULTIPLIER = 1;
    public static final int NORMAL_MULTIPLIER = 2;
    public static final int TREASURE_MULTIPLIER = 5;

    private ExperienceCost() {}

    public static long calculate(Map<Enchantment, Integer> currentLevels,
                                 Map<Enchantment, Integer> targetLevels) {
        long total = 0L;
        for (Map.Entry<Enchantment, Integer> entry : targetLevels.entrySet()) {
            int currentLevel = Math.max(0, currentLevels.getOrDefault(entry.getKey(), 0));
            total = addDeltaSaturated(total, currentLevel, entry.getValue(), multiplier(entry.getKey()));
        }
        return total;
    }

    public static int multiplier(Enchantment enchantment) {
        if (enchantment.isCurse()) {
            return CURSE_MULTIPLIER;
        }
        if (enchantment.isTreasureOnly()) {
            return TREASURE_MULTIPLIER;
        }
        return NORMAL_MULTIPLIER;
    }

    static long addDeltaSaturated(long runningTotal, int currentLevel, int targetLevel, int multiplier) {
        if (currentLevel < 0 || targetLevel < 0 || multiplier <= 0) {
            return runningTotal;
        }
        long amount = ((long) targetLevel - currentLevel) * multiplier;
        if (amount > 0L && runningTotal > Long.MAX_VALUE - amount) {
            return Long.MAX_VALUE;
        }
        if (amount < 0L && runningTotal < Long.MIN_VALUE - amount) {
            return Long.MIN_VALUE;
        }
        return runningTotal + amount;
    }
}
