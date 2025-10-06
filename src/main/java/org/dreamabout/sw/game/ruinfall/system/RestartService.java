package org.dreamabout.sw.game.ruinfall.system;
import org.dreamabout.sw.game.ruinfall.model.*;
public class RestartService {
    private final DungeonGenerator generator = new DungeonGenerator();
    public Dungeon restart(Dungeon old,long seed){
        return generator.generate(seed, old.getWidth(), old.getHeight());
    }
}
