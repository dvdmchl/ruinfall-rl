package org.dreamabout.sw.game.ruinfall.system;

import org.dreamabout.sw.game.ruinfall.system.EscActionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** T012: Telemetry logging toggle behavior */
public class ESCTelemetryLoggingTest {

    @Test
    @DisplayName("Telemetry disabled results in no recorded actions")
    void telemetryDisabledNoRecords() {
        TelemetryConfig.setEnabled(false); // expected API
        TelemetryLogger logger = new TelemetryLogger(() -> TelemetryConfig.isEnabled());
        logger.record(EscActionType.OPEN_MENU);
        assertEquals(0, logger.getRecords().size());
    }

    @Test
    @DisplayName("Telemetry enabled records actions")
    void telemetryEnabledRecords() {
        TelemetryConfig.setEnabled(true);
        TelemetryLogger logger = new TelemetryLogger(() -> TelemetryConfig.isEnabled());
        logger.record(EscActionType.OPEN_MENU);
        assertEquals(1, logger.getRecords().size());
    }
}

