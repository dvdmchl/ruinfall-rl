package org.dreamabout.sw.game.ruinfall.system;

/** Runtime telemetry enable/disable flag (mutable for tests). */
public final class TelemetryConfig {
    private static volatile boolean enabled = true;
    private TelemetryConfig() {}
    public static boolean isEnabled() { return enabled; }
    public static void setEnabled(boolean value) { enabled = value; }
}

