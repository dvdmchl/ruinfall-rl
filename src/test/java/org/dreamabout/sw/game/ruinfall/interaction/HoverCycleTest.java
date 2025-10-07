package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HoverCycleTest {
    @Test
    void cycleWrapsThroughStack() {
        InteractiveRegistry reg = new InteractiveRegistry();
        reg.register(new EnemyObjectAdapter("e1","Goblin","HP",1,1));
        reg.register(new NPC("n1","NPC","Friendly",1,1));
        reg.register(new Chest("c1","Chest","Loot",1,1));
        HoverManager hm = new HoverManager();
        hm.onHoverTile(1,1, reg);
        assertEquals("e1", hm.getState().hoveredId()); // enemy first
        hm.cycle(+1, reg); // to NPC
        assertEquals("n1", hm.getState().hoveredId());
        hm.cycle(+1, reg); // to Chest
        assertEquals("c1", hm.getState().hoveredId());
        hm.cycle(+1, reg); // wrap to enemy
        assertEquals("e1", hm.getState().hoveredId());
        hm.cycle(-1, reg); // backwards wrap to chest
        assertEquals("c1", hm.getState().hoveredId());
    }
}
