package org.dreamabout.sw.game.ruinfall.model;
public class Enemy {
    private int x,y,lastDamageTurn=-1; public Enemy(int x,int y){this.x=x;this.y=y;}
    public int getX(){return x;} public int getY(){return y;} public void setPosition(int x,int y){this.x=x;this.y=y;} public int getLastDamageTurn(){return lastDamageTurn;} public void setLastDamageTurn(int t){lastDamageTurn=t;}
}

