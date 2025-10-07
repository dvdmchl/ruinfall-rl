package org.dreamabout.sw.game.ruinfall.system;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class SeedUtilTest {

    @Test
    void sanitizeRemovesInvalidAndUppercases() {
        String in = "abC-123_!!xyz";
        String out = SeedUtil.sanitize(in);
        assertEquals("ABC123XYZ", out);
    }

    @Test
    void sanitizeLengthLimit() {
        String in = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // >16
        String out = SeedUtil.sanitize(in);
        assertEquals(16, out.length());
    }

    @Test
    void deriveDeterministic() {
        String seed = "ABC123";
        long a = SeedUtil.derive(seed);
        long b = SeedUtil.derive(seed);
        assertEquals(a, b, "Derive must be deterministic");
    }

    @Test
    void differentSeedsUsuallyDifferentHash() {
        long a = SeedUtil.derive("AAAAAA");
        long b = SeedUtil.derive("AAAABA");
        assertNotEquals(a, b, "Distinct typical seeds should produce different hashes (not guaranteeing collision-free)");
    }

    @Test
    void randomProducesValidSeed() {
        Random r = new Random(1234);
        String s = SeedUtil.random(r);
        assertTrue(s.length() >= 6 && s.length() <= 12);
        assertTrue(s.chars().allMatch(c -> Character.isLetterOrDigit(c)));
    }
}

