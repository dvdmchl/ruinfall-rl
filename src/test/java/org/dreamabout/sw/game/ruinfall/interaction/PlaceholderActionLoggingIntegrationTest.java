package org.dreamabout.sw.game.ruinfall.interaction;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class PlaceholderActionLoggingIntegrationTest {

    @Test
    void placeholderActionLogsAttempt() {
        InteractiveRegistry reg = new InteractiveRegistry();
        var enemy = new EnemyObjectAdapter("e1","Goblin","HP",3,3);
        reg.register(enemy);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InteractionUIController ui = new InteractionUIController(reg, new SelectionManager(), new HoverManager(), new PrintStream(baos));
        ui.onLeftClickTile(3,3, id -> true);
        ui.invokeAction("ATTACK");
        String out = baos.toString();
        assertTrue(out.contains("ACTION_PLACEHOLDER"));
        assertTrue(out.contains("objectId=e1"));
        assertTrue(out.contains("action=ATTACK"));
    }
}
