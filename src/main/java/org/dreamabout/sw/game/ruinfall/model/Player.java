package org.dreamabout.sw.game.ruinfall.model;
public class Player {
    private int x,y,hp,maxHp,lastDamageTurn=-1; public Player(int x,int y,int hp){this.x=x;this.y=y;this.hp=hp;this.maxHp=hp;}
    public int getX(){return x;} public int getY(){return y;} public void setPosition(int x,int y){this.x=x;this.y=y;} public int getHp(){return hp;} public int getMaxHp(){return maxHp;}
    public int getLastDamageTurn(){return lastDamageTurn;} public void setLastDamageTurn(int t){lastDamageTurn=t;} public void damage(int amount){hp-=amount; if(hp<0)hp=0;}
}

