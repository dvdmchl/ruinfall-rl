package org.dreamabout.sw.game.ruinfall.model;
public class Tile {
    private TileType type;
    private VisibilityState visibility;
    public Tile(TileType type) { this.type = type; this.visibility = VisibilityState.UNSEEN; }
    public TileType getType() { return type; }
    public VisibilityState getVisibility() { return visibility; }
    public void setVisibility(VisibilityState v) { this.visibility = v; }
}

