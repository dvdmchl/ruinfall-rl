package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * T004: Movement-trigger hover refresh (EXPECTED TO FAIL until T012 implemented).
 */
class HoverMovementRefreshIntegrationTest {

    @Test @DisplayName("Enemy moving onto stationary cursor tile adopts hover without mouse move")
    void enemyMovesOntoHoveredTile() {
        InteractiveRegistry reg = new InteractiveRegistry();
        HoverManager hm = new HoverManager();
        reg.addListener(hm);
        EnemyObjectAdapter enemy = new EnemyObjectAdapter("enemy-1","Goblin","HP",2,2);
        reg.register(enemy);
        hm.onHoverTile(3,2, reg); // stationary cursor over empty
        assertNull(hm.getState().hoveredId(), "Precondition: no hovered object");

        // Move enemy onto (3,2)
        enemy.setTilePosition(3,2); // adapter-first scenario
        reg.moveObject(enemy.getId(),3,2);

        // EXPECTED (future): hover auto-adopts enemy without another onHoverTile call
        assertEquals("enemy-1", hm.getState().hoveredId(), "Should adopt moving enemy");
    }

    @Test @DisplayName("Enemy moving off hovered tile clears or re-falls back when no others")
    void enemyMovesOffHoveredTile() {
        InteractiveRegistry reg = new InteractiveRegistry();
        HoverManager hm = new HoverManager();
        reg.addListener(hm);
        EnemyObjectAdapter enemy = new EnemyObjectAdapter("enemy-2","Goblin","HP",5,5);
        reg.register(enemy);
        hm.onHoverTile(5,5, reg);
        assertEquals("enemy-2", hm.getState().hoveredId(), "Precondition: enemy hovered");

        // Move enemy away
        enemy.setTilePosition(6,5);
        reg.moveObject(enemy.getId(),6,5);

        // EXPECTED (future): hover cleared automatically
        assertNull(hm.getState().hoveredId(), "Should clear hover after enemy leaves");
    }
}
