package org.dreamabout.sw.game.ruinfall.system;
import org.dreamabout.sw.game.ruinfall.model.*;
import java.util.*;
public class LOSCalculator {
    public Set<String> computeVisible(Dungeon d,int px,int py,int radius){
        Set<String> vis = new HashSet<>();
        vis.add(px+","+py);
        int r2 = radius*radius;
        for(int y=py-radius; y<=py+radius; y++){
            for(int x=px-radius; x<=px+radius; x++){
                int dx = x-px, dy = y-py;
                if(dx*dx+dy*dy>r2) continue;
                castRay(d, px, py, x, y, vis, radius);
            }
        }
        return vis;
    }
    private void castRay(Dungeon d,int x0,int y0,int x1,int y1,Set<String> vis,int radius){
        int dx = Math.abs(x1-x0), sx = x0<x1?1:-1;
        int dy = -Math.abs(y1-y0), sy = y0<y1?1:-1;
        int err = dx+dy;
        int x = x0; int y = y0; int steps=0;
        while(true){
            if(x>=0&&y>=0&&x<d.getWidth()&&y<d.getHeight()){
                vis.add(x+","+y);
                if(d.getTile(x,y).getType()==TileType.WALL && !(x==x0&&y==y0)) break;
            } else break;
            if(x==x1 && y==y1) break;
            int e2 = 2*err;
            if(e2>=dy){err+=dy; x+=sx;}
            if(e2<=dx){err+=dx; y+=sy;}
            steps++; if(steps>radius*2) break; // safety
        }
    }
}
