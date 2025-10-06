package org.dreamabout.sw.game.ruinfall.generation;
import org.dreamabout.sw.game.ruinfall.system.DungeonGenerator;
import org.dreamabout.sw.game.ruinfall.model.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
public class DungeonGeneratorConnectivityTest {
    @Test
    void allFloorReachableAndSufficient() {
        DungeonGenerator gen = new DungeonGenerator();
        Dungeon d = gen.generate(1234L, 40, 30);
        int floors = 0; for(int y=0;y<d.getHeight();y++) for(int x=0;x<d.getWidth();x++) if(d.getTile(x,y).getType()==TileType.FLOOR) floors++;
        assertTrue(floors > 50, "Expected at least 51 floor tiles, got "+floors); // should fail (placeholder small)
        // BFS from player spawn
        boolean[][] vis = new boolean[d.getHeight()][d.getWidth()];
        Deque<int[]> dq = new ArrayDeque<>(); dq.add(new int[]{d.getPlayerSpawnX(), d.getPlayerSpawnY()}); vis[d.getPlayerSpawnY()][d.getPlayerSpawnX()]=true;
        int reachable=0; int[][] dirs={{1,0},{-1,0},{0,1},{0,-1}};
        while(!dq.isEmpty()){
            int[] c = dq.removeFirst(); if(d.getTile(c[0],c[1]).getType()==TileType.FLOOR) reachable++;
            for(int[] dir:dirs){int nx=c[0]+dir[0], ny=c[1]+dir[1]; if(nx>=0&&ny>=0&&nx<d.getWidth()&&ny<d.getHeight()&&!vis[ny][nx]&&d.getTile(nx,ny).getType()==TileType.FLOOR){vis[ny][nx]=true; dq.add(new int[]{nx,ny});}}
        }
        assertEquals(floors, reachable, "All floor tiles must be reachable");
    }
}

