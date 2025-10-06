package org.dreamabout.sw.game.ruinfall.model;
import java.util.*;
public class Corridor {
    private final java.util.List<int[]> points = new ArrayList<>();
    public void addPoint(int x,int y){points.add(new int[]{x,y});}
    public List<int[]> getPoints(){return points;}
}

