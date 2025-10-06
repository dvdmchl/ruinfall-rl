package org.dreamabout.sw.game.ruinfall.system;
import org.dreamabout.sw.game.ruinfall.model.*;
import java.util.*;
public class EnemyAI {
    private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};
    public void moveEnemy(Dungeon d, Enemy e, Random rng){
        List<int[]> candidates = new ArrayList<>();
        for(int[] dir: DIRS){
            int nx = e.getX()+dir[0]; int ny = e.getY()+dir[1];
            if(nx>=0&&ny>=0&&nx<d.getWidth()&&ny<d.getHeight()&&d.getTile(nx,ny).getType()==TileType.FLOOR){
                candidates.add(new int[]{nx,ny});
            }
        }
        if(!candidates.isEmpty()){
            int[] pick = candidates.get(rng.nextInt(candidates.size()));
            e.setPosition(pick[0], pick[1]);
        }
    }
}
