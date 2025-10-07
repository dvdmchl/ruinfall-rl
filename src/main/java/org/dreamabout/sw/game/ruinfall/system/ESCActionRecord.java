package org.dreamabout.sw.game.ruinfall.system;

/** Immutable record of a single ESC action event. */
public record ESCActionRecord(EscActionType action, long timestampMs) {}

