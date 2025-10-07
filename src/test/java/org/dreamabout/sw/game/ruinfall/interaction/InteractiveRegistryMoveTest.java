package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InteractiveRegistryMoveTest {

    @Test
    void moveObjectUpdatesTileEvenIfAdapterUpdatedFirst() {
        InteractiveRegistry registry = new InteractiveRegistry();
        EnemyObjectAdapter enemy = new EnemyObjectAdapter("enemy-1", "Goblin", "HP 5/5", 2, 3);
        registry.register(enemy);

        // Adapter updated first, then registry notified
        enemy.setTilePosition(5, 6);
        registry.moveObject(enemy.getId(), 5, 6);

        var moved = registry.getPrimaryAt(5, 6);
        assertTrue(moved.isPresent(), "new tile should resolve object");
        assertEquals("enemy-1", moved.orElseThrow().getId());
        assertTrue(registry.getStackAt(2, 3).isEmpty(), "old tile should no longer contain object");
    }

    @Test
    void moveObjectUpdatesTileWhenRegistryDrivesUpdate() {
        InteractiveRegistry registry = new InteractiveRegistry();
        EnemyObjectAdapter enemy = new EnemyObjectAdapter("enemy-2", "Goblin", "HP 5/5", 1, 1);
        registry.register(enemy);

        // Registry drives movement (adapter position updated inside moveObject)
        registry.moveObject(enemy.getId(), 4, 4);

        var moved = registry.getPrimaryAt(4, 4);
        assertTrue(moved.isPresent(), "new tile should resolve object");
        assertEquals("enemy-2", moved.orElseThrow().getId());
        assertTrue(registry.getStackAt(1, 1).isEmpty(), "old tile should be empty after move");
    }
}
