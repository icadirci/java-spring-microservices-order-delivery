package com.orderplatform.catalogservice.common;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugUtils {

    private static final Locale TR = Locale.forLanguageTag("tr");

    private SlugUtils() {
    }

    /**
     * "Çok Güzel Ürün!" -> "cok-guzel-urun"
     */
    public static String slugify(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Slug için metin boş olamaz");
        }
        String s = input.trim().toLowerCase(TR).replace('ı', 'i');
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        s = s.replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Metinden geçerli bir slug üretilemedi");
        }
        return s.length() > 500 ? s.substring(0, 500) : s;
    }
}
