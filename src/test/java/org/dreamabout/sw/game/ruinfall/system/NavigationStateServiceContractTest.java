package org.dreamabout.sw.game.ruinfall.system;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Contract test (T004 updated): NavigationStateService behavior. */
class NavigationStateServiceContractTest {

    @Test
    @DisplayName("Open -> depth increments; navigateBack decrements; resume closes")
    void navigationFlow() {
        NavigationStateService svc = new NavigationStateService();
        assertFalse(svc.isPauseMenuOpen());
        svc.openPauseMenu();
        assertTrue(svc.isPauseMenuOpen());
        assertEquals(1, svc.getMenuDepth());
        svc.openPauseMenu();
        assertEquals(2, svc.getMenuDepth());
        svc.navigateBack();
        assertEquals(1, svc.getMenuDepth());
        svc.navigateBack();
        assertFalse(svc.isPauseMenuOpen());
    }
}
