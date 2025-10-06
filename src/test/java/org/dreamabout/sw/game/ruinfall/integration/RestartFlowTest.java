package org.dreamabout.sw.game.ruinfall.integration;
import org.dreamabout.sw.game.ruinfall.system.*;
import org.dreamabout.sw.game.ruinfall.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class RestartFlowTest {
    @Test
    void restartProducesNewDungeonOrResetsState() {
        DungeonGenerator gen = new DungeonGenerator();
        long seed = 999L;
        Dungeon d1 = gen.generate(seed, 20, 15);
        RestartService rs = new RestartService();
        Dungeon d2 = rs.restart(d1, seed);
        // Expect a different instance or at least changed seed/structure
        assertNotSame(d1, d2, "Restart should produce a new dungeon instance (placeholder returns same)");
    }
}

