package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * T003: Coordinate mapping contract tests (expected to FAIL before T008 implementation).
 */
class HoverCoordinateMappingTest {

    @Test @DisplayName("Origin maps to (0,0)")
    void origin() {
        var tc = HoverMath.mapCursorToTile(0,0,0,0,32);
        assertEquals(0, tc.x());
        assertEquals(0, tc.y());
    }

    @Test @DisplayName("Boundary 31.9 inside first tile")
    void boundaryFloor() {
        var tc = HoverMath.mapCursorToTile(31.9,0,0,0,32);
        assertEquals(0, tc.x());
    }

    @Test @DisplayName("Switch at 32 -> tile 1")
    void firstSwitch() {
        var tc = HoverMath.mapCursorToTile(32,0,0,0,32);
        assertEquals(1, tc.x());
    }

    @Test @DisplayName("Positive offset inside")
    void positiveOffsetInside() {
        var tc = HoverMath.mapCursorToTile(15,0,16,0,32); // worldX=31
        assertEquals(0, tc.x());
    }

    @Test @DisplayName("Positive offset crossing")
    void positiveOffsetCrossing() {
        var tc = HoverMath.mapCursorToTile(15,0,20,0,32); // worldX=35
        assertEquals(1, tc.x());
    }

    @Test @DisplayName("Negative offset -> sentinel (-1,-1)")
    void negativeOffsetSentinel() {
        var tc = HoverMath.mapCursorToTile(5,0,-6,0,32); // worldX=-1
        assertEquals(-1, tc.x());
        assertEquals(-1, tc.y());
    }

    @Test @DisplayName("Fractional safe")
    void fractionalSafe() {
        var tc = HoverMath.mapCursorToTile(10,0,0.9,0,32); // worldX=10.9
        assertEquals(0, tc.x());
    }

    @Test @DisplayName("Fractional crossing")
    void fractionalCrossing() {
        var tc = HoverMath.mapCursorToTile(10,0,31.2,0,32); // worldX=41.2
        assertEquals(1, tc.x());
    }
}

