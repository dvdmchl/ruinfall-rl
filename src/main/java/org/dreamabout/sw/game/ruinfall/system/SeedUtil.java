package org.dreamabout.sw.game.ruinfall.system;

import java.util.Random;

/**
 * Utility for world seed string handling and deterministic long derivation.
 * Matches feature spec FR-001..FR-013 (sanitization, length limit, uppercase, determinism).
 */
public final class SeedUtil {
    private SeedUtil() {}

    private static final long FNV_OFFSET = 0xcbf29ce484222325L;
    private static final long FNV_PRIME = 0x100000001b3L;

    /** Sanitize raw input to uppercase alphanumeric, max 16 chars. */
    public static String sanitize(String raw) {
        if (raw == null) return "";
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < raw.length() && sb.length() < 16; i++) {
            char c = raw.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }

    /** Derive deterministic 64-bit seed from sanitized seed string via FNV-1a. */
    public static long derive(String seedStr) {
        String s = seedStr == null ? "" : seedStr;
        long hash = FNV_OFFSET;
        for (int i = 0; i < s.length(); i++) {
            hash ^= s.charAt(i);
            hash *= FNV_PRIME;
        }
        return hash;
    }

    /** Convert long to canonical uppercase base36 (≤16 chars). */
    public static String toSeedStringFromLong(long value) {
        String s = Long.toUnsignedString(value, 36).toUpperCase();
        if (s.length() > 16) s = s.substring(0, 16);
        return s;
    }

    /** Generate a random seed string (6–12 chars) then sanitize (defensive). */
    public static String random(Random rng) {
        long v = Math.abs(rng.nextLong());
        String s = Long.toUnsignedString(v, 36).toUpperCase();
        if (s.length() > 12) s = s.substring(0, 12);
        if (s.length() < 6) {
            // pad by concatenating another random chunk
            long v2 = Math.abs(rng.nextLong());
            String s2 = Long.toUnsignedString(v2, 36).toUpperCase();
            s = (s + s2);
            if (s.length() > 12) s = s.substring(0, 12);
        }
        return sanitize(s);
    }
}

