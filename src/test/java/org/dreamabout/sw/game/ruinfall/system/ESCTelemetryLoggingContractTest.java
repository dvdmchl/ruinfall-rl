package org.dreamabout.sw.game.ruinfall.system;

import org.dreamabout.sw.game.ruinfall.system.EscActionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Contract test (T007 updated): Telemetry logger API surface */
class ESCTelemetryLoggingContractTest {

    @Test
    @DisplayName("Telemetry logger records only when enabled")
    void telemetryFlagRespected() {
        TelemetryConfig.setEnabled(false);
        TelemetryLogger logger = new TelemetryLogger(TelemetryConfig::isEnabled);
        logger.record(EscActionType.OPEN_MENU);
        assertEquals(0, logger.getRecords().size());
        TelemetryConfig.setEnabled(true);
        logger.record(EscActionType.OPEN_MENU);
        assertEquals(1, logger.getRecords().size());
    }
}
