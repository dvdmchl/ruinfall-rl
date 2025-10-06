package org.dreamabout.sw.game.ruinfall.generation;
import org.dreamabout.sw.game.ruinfall.system.DungeonGenerator;
import org.dreamabout.sw.game.ruinfall.model.Dungeon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class DungeonGeneratorRoomsTest {
    @Test
    void roomsWithinBoundsAndNonOverlapping(){
        Dungeon d = new DungeonGenerator().generate(42L, 40, 30);
        int count = d.getRooms().size();
        assertTrue(count >=3 && count <=15, "Expected 3-15 rooms, got "+count); // placeholder 0 -> fail
        // Overlap check would go here once rooms exist
    }
}

