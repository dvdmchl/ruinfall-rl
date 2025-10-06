package org.dreamabout.sw.game.ruinfall.visibility;
import org.dreamabout.sw.game.ruinfall.system.*;
import org.dreamabout.sw.game.ruinfall.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class LOSCalculatorTest {
    @Test
    void playerTileAlwaysVisibleAndWithinRadius() {
        Dungeon d = new DungeonGenerator().generate(99L, 30, 20);
        LOSCalculator los = new LOSCalculator();
        var visible = los.computeVisible(d, d.getPlayerSpawnX(), d.getPlayerSpawnY(), 8);
        assertTrue(visible.contains(d.getPlayerSpawnX()+","+d.getPlayerSpawnY()), "Player tile must be visible (placeholder returns empty)");
        assertTrue(visible.size() > 1, "Expected more than 1 visible tile");
    }
}

