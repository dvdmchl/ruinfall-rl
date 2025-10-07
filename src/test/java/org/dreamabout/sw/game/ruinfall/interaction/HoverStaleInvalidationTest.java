package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HoverStaleInvalidationTest {

    @Test @DisplayName("Removing hovered object falls back to next stack object")
    void removalFallback() {
        InteractiveRegistry reg = new InteractiveRegistry();
        HoverManager hm = new HoverManager();
        reg.addListener(hm);
        var enemy = new EnemyObjectAdapter("e1","Enemy","HP",1,1);
        var npc = new NPC("n1","NPC","Hi",1,1);
        reg.register(enemy); reg.register(npc);
        hm.onHoverTile(1,1, reg);
        assertEquals("e1", hm.getState().hoveredId());
        reg.unregister("e1");
        assertEquals("n1", hm.getState().hoveredId(), "Should fallback to next object");
    }

    @Test @DisplayName("Moving hovered object off tile clears hover when no fallback")
    void moveClearsWhenNoFallback() {
        InteractiveRegistry reg = new InteractiveRegistry();
        HoverManager hm = new HoverManager();
        reg.addListener(hm);
        var chest = new Chest("c1","Chest","Loot",2,2);
        reg.register(chest);
        hm.onHoverTile(2,2, reg);
        assertEquals("c1", hm.getState().hoveredId());
        chest.setTilePosition(3,2);
        reg.moveObject("c1",3,2);
        assertNull(hm.getState().hoveredId(), "Hover should clear after sole object leaves");
    }
}
