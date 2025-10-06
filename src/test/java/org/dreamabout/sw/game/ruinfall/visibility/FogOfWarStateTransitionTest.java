package org.dreamabout.sw.game.ruinfall.visibility;
import org.dreamabout.sw.game.ruinfall.system.*;
import org.dreamabout.sw.game.ruinfall.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class FogOfWarStateTransitionTest {
    @Test
    void visibilityTransitionsOccur() {
        Dungeon d = new DungeonGenerator().generate(77L, 20, 15);
        // set some tiles floor manually for test expectation
        d.setPlayerSpawn(0,0);
        VisibilitySystem vs = new VisibilitySystem();
        vs.recomputeVisibility(d, 0,0,8);
        // Expect at least one tile visibility not UNSEEN
        int changed=0; for(int y=0;y<d.getHeight();y++) for(int x=0;x<d.getWidth();x++) if(d.getTile(x,y).getVisibility()!=VisibilityState.UNSEEN) changed++;
        assertTrue(changed>0, "Expected some tiles to change visibility (placeholder does nothing)");
    }
}

