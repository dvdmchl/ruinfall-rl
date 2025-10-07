package org.dreamabout.sw.game.ruinfall.system;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** T011: Navigation back logic and depth handling */
public class PauseMenuBackNavigationTest {

    @Test
    @DisplayName("Submenu depth decrements and resume after root back")
    void backNavigationAndResume() {
        NavigationStateService nav = new NavigationStateService();
        nav.openPauseMenu();
        assertTrue(nav.isPauseMenuOpen());
        assertEquals(1, nav.getMenuDepth());
        // Simulate opening submenu
        nav.openPauseMenu();
        assertEquals(2, nav.getMenuDepth());
        nav.navigateBack();
        assertEquals(1, nav.getMenuDepth());
        nav.navigateBack(); // back from root => resume
        assertFalse(nav.isPauseMenuOpen());
    }
}

