package org.dreamabout.sw.game.ruinfall.interaction;

import org.dreamabout.sw.game.ruinfall.ui.ContextMenuNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextMenuPlacementTest {

    @Test
    void prefersRightSideWhenSpaceAvailable() {
        double anchorX = 100;
        double[] pos = ContextMenuNode.computePlacement(anchorX, 120, 800, 600, 0, 140, 80);
        assertTrue(pos[0] > anchorX, "Menu should appear to the right when space permits");
    }

    @Test
    void fallsBackLeftWhenSidePanelBlocks() {
        double anchorX = 380;
        double sidePanelStartX = 400;
        double[] pos = ContextMenuNode.computePlacement(anchorX, 200, 640, 480, sidePanelStartX, 160, 80);
        assertTrue(pos[0] < anchorX, "Menu should flip to the left when side panel blocks the right side");
        assertTrue(pos[0] + 160 <= sidePanelStartX - 4, "Menu must respect side panel margin");
    }

    @Test
    void clampsVerticallyInsideViewport() {
        double[] pos = ContextMenuNode.computePlacement(200, 30, 640, 300, 0, 180, 120);
        assertTrue(pos[1] >= 4, "Menu should clamp within top margin when anchor is near top");
    }

    @Test
    void clampsToBottomWhenBelowWouldOverflow() {
        double anchorY = 10;
        double menuHeight = 480;
        double viewportHeight = 500;
        double[] pos = ContextMenuNode.computePlacement(220, anchorY, 640, viewportHeight, 0, 160, menuHeight);
        assertEquals(viewportHeight - menuHeight - 4, pos[1], 0.0001,
                "Menu should clamp to bottom margin when below would overflow");
    }
}