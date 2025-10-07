package org.dreamabout.sw.game.ruinfall.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

/** Stub TelemetryLogger (T035) - logic will be finalized later. */
public class TelemetryLogger {
    private final BooleanSupplier enabledSupplier;
    private final List<ESCActionRecord> records = new ArrayList<>();

    public TelemetryLogger(BooleanSupplier enabledSupplier) {
        this.enabledSupplier = enabledSupplier;
    }

    public void record(EscActionType action) {
        if (!enabledSupplier.getAsBoolean()) return; // gating already; passes basic tests once enabled
        records.add(new ESCActionRecord(action, System.currentTimeMillis()));
    }

    public List<ESCActionRecord> getRecords() { return Collections.unmodifiableList(records); }
    public void clear() { records.clear(); }
}

