package org.dreamabout.sw.game.ruinfall.ai;
import org.dreamabout.sw.game.ruinfall.system.*;
import org.dreamabout.sw.game.ruinfall.model.*;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
public class EnemyMovementTest {
    @Test
    void enemyMovesOccasionallyAndStepWithinBounds() {
        Dungeon d = new DungeonGenerator().generate(55L, 20, 15);
        Enemy e = new Enemy(d.getEnemySpawnX(), d.getEnemySpawnY());
        EnemyAI ai = new EnemyAI();
        Random rng = new Random(1L);
        boolean moved = false;
        for(int i=0;i<20;i++){
            int beforeX = e.getX(); int beforeY = e.getY();
            ai.moveEnemy(d,e,rng);
            int dx = Math.abs(e.getX()-beforeX); int dy = Math.abs(e.getY()-beforeY);
            assertTrue(dx+dy <= 1, "Enemy should move at most one tile");
            if(dx+dy==1) moved = true;
        }
        assertTrue(moved, "Enemy should have moved at least once (placeholder never moves)");
    }
}

