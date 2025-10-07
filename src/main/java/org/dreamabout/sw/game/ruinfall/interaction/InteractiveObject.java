package org.dreamabout.sw.game.ruinfall.interaction;

/**
 * Core contract for any interactive object on the map.
 */
public interface InteractiveObject {

    String getId();

    String getDisplayName();

    InteractiveObjectType getType();

    /**
     * Short one-line summary (e.g. HP info or descriptor) for nameplate / side panel.
     */
    String getShortSummary();

    int getTileX();

    int getTileY();

    void setTilePosition(int x, int y);
}
