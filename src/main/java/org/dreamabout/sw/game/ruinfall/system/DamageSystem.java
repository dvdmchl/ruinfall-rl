package org.dreamabout.sw.game.ruinfall.system;
import org.dreamabout.sw.game.ruinfall.model.*;
public class DamageSystem {
    public void applyContactDamage(Player p, Enemy e, TurnSystem turn){
        if (p.getX()==e.getX() && p.getY()==e.getY()) {
            if(p.getLastDamageTurn() < turn.getCurrentTurn()) {
                p.damage(1);
                p.setLastDamageTurn(turn.getCurrentTurn());
            }
        }
    }
}
