package org.dreamabout.sw.game.ruinfall.interaction;

/**
 * Immutable value describing a single interactive action entry for menus/panels.
 */
public record InteractiveActionItem(String code, String labelKey, boolean enabled) {
}
