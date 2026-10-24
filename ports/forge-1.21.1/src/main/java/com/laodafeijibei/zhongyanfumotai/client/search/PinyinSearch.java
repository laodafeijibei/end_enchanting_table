package com.laodafeijibei.zhongyanfumotai.client.search;

import com.ibm.icu.text.Transliterator;

import java.util.Locale;

/** Lightweight search normalization used by the enchantment picker. */
public final class PinyinSearch {
    private static final Transliterator HAN_TO_LATIN = Transliterator.getInstance(
            "Han-Latin; NFD; [:Nonspacing Mark:] Remove; NFC");

    private PinyinSearch() {
    }

    public static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return HAN_TO_LATIN.transliterate(value)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]", "");
    }

    public static boolean matches(String query, String... values) {
        String needle = normalize(query);
        if (needle.isEmpty()) {
            return true;
        }
        for (String value : values) {
            String normalized = normalize(value);
            if (normalized.contains(needle) || value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}
