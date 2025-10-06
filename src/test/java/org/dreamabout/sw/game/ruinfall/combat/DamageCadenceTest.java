package org.dreamabout.sw.game.ruinfall.combat;
import org.dreamabout.sw.game.ruinfall.system.*;
import org.dreamabout.sw.game.ruinfall.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class DamageCadenceTest {
    @Test
    void damageOnlyOncePerTurn() {
        Player p = new Player(0,0,5);
        Enemy e = new Enemy(0,0);
        TurnSystem turn = new TurnSystem();
        DamageSystem dmg = new DamageSystem();
        dmg.applyContactDamage(p,e,turn);
        int afterFirst = p.getHp();
        dmg.applyContactDamage(p,e,turn);
        assertEquals(afterFirst, p.getHp(), "Damage should apply only once per turn (placeholder always damages)");
    }
}

