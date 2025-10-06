package org.dreamabout.sw.game.ruinfall.model;
public class Room {
    private int x,y,width,height; private int centerX, centerY;
    public Room(int x,int y,int width,int height){this.x=x;this.y=y;this.width=width;this.height=height;centerX=x+width/2;centerY=y+height/2;}
    public int getX(){return x;} public int getY(){return y;} public int getWidth(){return width;} public int getHeight(){return height;} public int getCenterX(){return centerX;} public int getCenterY(){return centerY;}
}

