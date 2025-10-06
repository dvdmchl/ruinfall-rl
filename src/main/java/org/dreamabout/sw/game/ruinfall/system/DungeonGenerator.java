package org.dreamabout.sw.game.ruinfall.system;
import org.dreamabout.sw.game.ruinfall.model.*;
import java.util.*;
public class DungeonGenerator {
    private static final int MIN_ROOM_W = 5;
    private static final int MIN_ROOM_H = 4;
    private static final int MAX_ROOM_W = 12;
    private static final int MAX_ROOM_H = 10;
    private static final int MAX_ATTEMPTS = 200;
    private static final int TARGET_ROOMS_MIN = 5;
    private static final int TARGET_ROOMS_MAX = 10;

    public Dungeon generate(long seed,int width,int height){
        Random rng = new Random(seed);
        Dungeon d = new Dungeon(width,height,seed);
        List<Room> placed = new ArrayList<>();
        int attempts = 0;
        while(attempts < MAX_ATTEMPTS && placed.size() < TARGET_ROOMS_MAX){
            attempts++;
            int w = MIN_ROOM_W + rng.nextInt(MAX_ROOM_W - MIN_ROOM_W +1);
            int h = MIN_ROOM_H + rng.nextInt(MAX_ROOM_H - MIN_ROOM_H +1);
            int x = rng.nextInt(Math.max(1,width - w -1))+1; // leave border wall
            int y = rng.nextInt(Math.max(1,height - h -1))+1;
            Room candidate = new Room(x,y,w,h);
            if(overlaps(candidate, placed)) continue;
            placed.add(candidate);
            d.addRoom(candidate);
            d.carveRoom(candidate);
            if(placed.size() >= TARGET_ROOMS_MIN && rng.nextDouble() < 0.25) break; // early stop chance
        }
        // ensure at least TARGET_ROOMS_MIN rooms; if not, carve extra fallback corridor area around spawn
        if(placed.isEmpty()){
            Room fallback = new Room(1,1,MIN_ROOM_W,MIN_ROOM_H);
            placed.add(fallback); d.addRoom(fallback); d.carveRoom(fallback);
        }
        connectRooms(d, placed);
        // Spawns
        Room first = placed.get(0);
        d.setPlayerSpawn(first.getCenterX(), first.getCenterY());
        Room last = placed.get(placed.size()-1);
        int ex = last.getCenterX(); int ey = last.getCenterY();
        if(ex == d.getPlayerSpawnX() && ey == d.getPlayerSpawnY() && placed.size()>1){
            Room alt = placed.get(Math.min(1, placed.size()-1));
            ex = alt.getCenterX(); ey = alt.getCenterY();
        }
        d.setEnemySpawn(ex, ey);
        return d;
    }

    private boolean overlaps(Room r, List<Room> rooms){
        for(Room o: rooms){
            if(r.getX() < o.getX()+o.getWidth()+1 && r.getX()+r.getWidth()+1 > o.getX() &&
               r.getY() < o.getY()+o.getHeight()+1 && r.getY()+r.getHeight()+1 > o.getY()){
                return true;
            }
        }
        return false;
    }

    private void connectRooms(Dungeon d, List<Room> rooms){
        if(rooms.size()<2) return;
        // Sort by center X for simple chain connectivity
        rooms.sort(Comparator.comparingInt(Room::getCenterX));
        for(int i=0;i<rooms.size()-1;i++){
            Room a = rooms.get(i); Room b = rooms.get(i+1);
            carveCorridor(d, a.getCenterX(), a.getCenterY(), b.getCenterX(), b.getCenterY());
        }
    }

    private void carveCorridor(Dungeon d, int x1,int y1,int x2,int y2){
        int cx = x1; int cy = y1;
        while(cx != x2){ d.carveFloor(cx, cy); cx += (x2>cx?1:-1); }
        while(cy != y2){ d.carveFloor(cx, cy); cy += (y2>cy?1:-1); }
        d.carveFloor(x2,y2);
    }
}
