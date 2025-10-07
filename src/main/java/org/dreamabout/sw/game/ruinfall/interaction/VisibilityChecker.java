package org.dreamabout.sw.game.ruinfall.interaction;

@FunctionalInterface
public interface VisibilityChecker {
    boolean isVisible(String objectId);
}

