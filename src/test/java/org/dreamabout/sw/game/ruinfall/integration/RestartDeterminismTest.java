package org.dreamabout.sw.game.ruinfall.integration;
import org.dreamabout.sw.game.ruinfall.model.Dungeon;
import org.dreamabout.sw.game.ruinfall.system.DungeonGenerator;
import org.dreamabout.sw.game.ruinfall.system.RestartService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RestartDeterminismTest {
    private String hash(Dungeon d){
        StringBuilder sb = new StringBuilder();
        for(int y=0;y<d.getHeight();y++){
            for(int x=0;x<d.getWidth();x++) sb.append(d.getTile(x,y).getType().ordinal());
            sb.append('\n');
        }
        return sb.toString();
    }
    @Test
    void restartWithSameSeedReproducesLayout(){
        long seed = 123456789L;
        DungeonGenerator gen = new DungeonGenerator();
        Dungeon first = gen.generate(seed, 48, 32);
        String h1 = hash(first);
        RestartService rs = new RestartService();
        Dungeon second = rs.restart(first, seed);
        assertNotSame(first, second, "Should be a distinct instance");
        String h2 = hash(second);
        assertEquals(h1, h2, "Dungeon layout must match for identical seed restart");
    }
}

