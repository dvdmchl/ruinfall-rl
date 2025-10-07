package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistryPerformanceTest {

    @Test
    void lookupAverageUnderPointOneMillis() {
        InteractiveRegistry registry = new InteractiveRegistry();
        int index = 0;
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                registry.register(new EnemyObjectAdapter("enemy-" + index, "Enemy" + index, "", x, y));
                registry.register(new NPC("npc-" + index, "NPC" + index, "", x, y));
                registry.register(new Chest("chest-" + index, "Chest" + index, "", x, y));
                index++;
            }
        }

        int iterations = 10_000;
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int x = i % 20;
            int y = (i / 20) % 20;
            registry.getStackAt(x, y);
            registry.getPrimaryAt(x, y);
        }
        long elapsed = System.nanoTime() - start;
        double avgMillis = (elapsed / 1_000_000.0) / iterations;
        assertTrue(avgMillis < 0.1, "Average registry lookup should remain under 0.1 ms, but was " + avgMillis);
    }
}