package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InteractiveResolutionTest {
    @Test
    void priorityOrderingEnemyNpcChestStableInsertion() {
        InteractiveRegistry reg = new InteractiveRegistry();
        // Insert chest first, then npc, then enemy at same tile (5,7)
        reg.register(new Chest("c1","Chest","Old chest",5,7));
        reg.register(new NPC("n1","Villager","Friendly",5,7));
        reg.register(new EnemyObjectAdapter("e1","Goblin","HP 5/5",5,7));
        var stack = reg.getStackAt(5,7);
        assertEquals(3, stack.size());
        assertEquals(InteractiveObjectType.ENEMY, stack.get(0).getType(), "Enemy should be first");
        assertEquals(InteractiveObjectType.NPC, stack.get(1).getType(), "NPC second");
        assertEquals(InteractiveObjectType.CHEST, stack.get(2).getType(), "Chest last");

        // Add another enemy later should appear after first enemy but before others
        reg.register(new EnemyObjectAdapter("e2","Orc","HP 10/10",5,7));
        stack = reg.getStackAt(5,7);
        assertEquals(4, stack.size());
        assertEquals("e1", stack.get(0).getId());
        assertEquals("e2", stack.get(1).getId());
    }
}
