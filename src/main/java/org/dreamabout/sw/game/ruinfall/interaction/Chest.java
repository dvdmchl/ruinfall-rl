package org.dreamabout.sw.game.ruinfall.interaction;

import java.util.Objects;

public class Chest implements InteractiveObject {
    private final String id;
    private final String displayName;
    private final String summary;
    private int tileX;
    private int tileY;

    public Chest(String id, String displayName, String summary, int tileX, int tileY) {
        this.id = Objects.requireNonNull(id);
        this.displayName = Objects.requireNonNull(displayName);
        this.summary = Objects.requireNonNull(summary);
        this.tileX = tileX; this.tileY = tileY;
    }

    @Override public String getId() { return id; }
    @Override public String getDisplayName() { return displayName; }
    @Override public InteractiveObjectType getType() { return InteractiveObjectType.CHEST; }
    @Override public String getShortSummary() { return summary; }
    @Override public int getTileX() { return tileX; }
    @Override public int getTileY() { return tileY; }
    @Override public void setTilePosition(int x, int y) { this.tileX = x; this.tileY = y; }
}
