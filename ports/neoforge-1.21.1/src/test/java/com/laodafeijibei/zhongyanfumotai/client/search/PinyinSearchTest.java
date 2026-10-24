package com.laodafeijibei.zhongyanfumotai.client.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PinyinSearchTest {
    @Test
    void convertsChineseToSearchablePinyin() {
        assertEquals("baohu", PinyinSearch.normalize("保护"));
        assertTrue(PinyinSearch.matches("baohu", "保护"));
    }

    @Test
    void keepsResourceIdsAndEnglishNamesSearchable() {
        assertTrue(PinyinSearch.matches("minecraft:protection", "minecraft:protection", "保护"));
        assertTrue(PinyinSearch.matches("protect", "Protection"));
    }
}
