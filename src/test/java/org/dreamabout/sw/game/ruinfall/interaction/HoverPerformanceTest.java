package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * T015: Guard that hover refresh logic only inspects active hovered tile stack (O(k) where k=stack size).
 */
class HoverPerformanceTest {

    static class SpyRegistry extends InteractiveRegistry {
        int stackCalls = 0; int lastX; int lastY;
        @Override
        public java.util.List<InteractiveObject> getStackAt(int x, int y) {
            stackCalls++; lastX = x; lastY = y;
            return super.getStackAt(x, y);
        }
    }

    @Test @DisplayName("Movement of non-hovered object does not trigger extra stack scans")
    void movementElsewhereDoesNotScanOtherTiles() {
        SpyRegistry reg = new SpyRegistry();
        HoverManager hm = new HoverManager();
        reg.addListener(hm);
        // Populate many objects on different tiles
        for (int i=0;i<200;i++) {
            int x = i % 40; int y = (i / 40); // small grid
            reg.register(new Chest("c"+i, "Chest"+i, "Loot", x, y));
        }
        // Hover a specific tile (5,1)
        hm.onHoverTile(5,1, reg);
        int callsAfterHover = reg.stackCalls; // exactly 1 expected
        assertTrue(callsAfterHover >=1, "Precondition: at least one stack call for initial hover");
        // Move an object on a different tile (10,1)->(11,1)
        // Find that chest id
        String moveId = "c" + (1*40 + 10); // x=10,y=1
        Chest moved = (Chest) reg.getById(moveId);
        moved.setTilePosition(11,1);
        reg.moveObject(moveId,11,1);
        // Since hover tile not affected, no new stack call should have happened
        assertEquals(callsAfterHover, reg.stackCalls, "No additional stack scans expected for unrelated movement");
    }
}

