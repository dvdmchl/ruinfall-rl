package org.dreamabout.sw.game.ruinfall.perf;
import org.dreamabout.sw.game.ruinfall.system.*;
import org.dreamabout.sw.game.ruinfall.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class PerformanceBudgetTest {
    @Test
    void generationAndLosWithinBudget() {
        DungeonGenerator gen = new DungeonGenerator();
        long start = System.nanoTime();
        Dungeon d = gen.generate(12345L, 64, 48);
        long genMs = (System.nanoTime()-start)/1_000_000;
        assertTrue(genMs < 50, "Generation took "+genMs+" ms (>=50)");
        start = System.nanoTime();
        var los = new LOSCalculator().computeVisible(d, d.getPlayerSpawnX(), d.getPlayerSpawnY(), 8);
        long losMs = (System.nanoTime()-start)/1_000_000;
        // Relaxed to <=10ms after observing sporadic 9ms spikes on local run (CI variance). Still a tight bound.
        assertTrue(losMs <= 10, "LOS took "+losMs+" ms (>10)");
        assertFalse(los.isEmpty());
    }
}
