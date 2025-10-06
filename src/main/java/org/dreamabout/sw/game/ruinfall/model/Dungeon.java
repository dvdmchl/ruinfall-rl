package org.dreamabout.sw.game.ruinfall.model;
import java.util.*;
public class Dungeon {
    private final int width; private final int height; private final Tile[][] tiles; private final List<Room> rooms = new ArrayList<>(); private final long seed; private int playerSpawnX=0, playerSpawnY=0, enemySpawnX=0, enemySpawnY=0;
    public Dungeon(int width,int height,long seed){
        this.width=width;this.height=height;this.seed=seed;tiles=new Tile[height][width];
        for(int y=0;y<height;y++) for(int x=0;x<width;x++) tiles[y][x]=new Tile(TileType.WALL);
    }
    public int getWidth(){return width;} public int getHeight(){return height;} public Tile getTile(int x,int y){return tiles[y][x];} public List<Room> getRooms(){return rooms;} public long getSeed(){return seed;}
    public void addRoom(Room r){rooms.add(r);}
    public void carveRoom(Room r){
        for(int y=r.getY(); y< r.getY()+r.getHeight(); y++){
            for(int x=r.getX(); x< r.getX()+r.getWidth(); x++){
                if(inBounds(x,y)) tiles[y][x]=new Tile(TileType.FLOOR);
            }
        }
    }
    public void carveFloor(int x,int y){ if(inBounds(x,y)) tiles[y][x]=new Tile(TileType.FLOOR); }
    private boolean inBounds(int x,int y){return x>=0&&y>=0&&x<width&&y<height;}
    public int getPlayerSpawnX(){return playerSpawnX;} public int getPlayerSpawnY(){return playerSpawnY;} public int getEnemySpawnX(){return enemySpawnX;} public int getEnemySpawnY(){return enemySpawnY;}
    public void setPlayerSpawn(int x,int y){playerSpawnX=x;playerSpawnY=y; carveFloor(x,y);} public void setEnemySpawn(int x,int y){enemySpawnX=x;enemySpawnY=y; carveFloor(x,y);}
}
