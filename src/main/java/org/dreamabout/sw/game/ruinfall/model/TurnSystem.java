package org.dreamabout.sw.game.ruinfall.model;
public class TurnSystem {
    private int currentTurn=0; private boolean runEnded=false;
    public int getCurrentTurn(){return currentTurn;} public void nextTurn(){currentTurn++;} public boolean isRunEnded(){return runEnded;} public void endRun(){runEnded=true;}
    public void reset(){ currentTurn = 0; runEnded = false; }
}
