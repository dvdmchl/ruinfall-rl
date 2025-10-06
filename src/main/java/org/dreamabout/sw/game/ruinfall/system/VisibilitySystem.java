package org.dreamabout.sw.game.ruinfall.system;
import org.dreamabout.sw.game.ruinfall.model.*;
import java.util.*;
public class VisibilitySystem {
    private final LOSCalculator los = new LOSCalculator();
    public void recomputeVisibility(Dungeon d,int px,int py,int radius){
        // demote VISIBLE -> MEMORY
        for(int y=0;y<d.getHeight();y++) for(int x=0;x<d.getWidth();x++) {
            Tile tile = d.getTile(x,y);
            if(tile.getVisibility()==VisibilityState.VISIBLE) tile.setVisibility(VisibilityState.MEMORY);
        }
        Set<String> visible = los.computeVisible(d, px, py, radius);
        for(String coord: visible){
            String[] parts = coord.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            if(x>=0&&y>=0&&x<d.getWidth()&&y<d.getHeight()){
                d.getTile(x,y).setVisibility(VisibilityState.VISIBLE);
            }
        }
    }
}
