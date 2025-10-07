package org.dreamabout.sw.game.ruinfall.system;

/**
 * Gameplay-wide configuration access point.
 * For now only exposes telemetry flag; later can delegate to TelemetryConfig once implemented.
 */
public final class GameplayConfig {
    private GameplayConfig() {}

    // Default true per task T002 until TelemetryConfig is introduced.
    private static final boolean TELEMETRY_ENABLED = true;

    public static boolean isTelemetryEnabled() {
        return TELEMETRY_ENABLED;
    }
}

