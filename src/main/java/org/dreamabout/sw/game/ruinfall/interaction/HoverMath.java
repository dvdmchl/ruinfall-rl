package org.dreamabout.sw.game.ruinfall.interaction;

/**
 * Pure deterministic coordinate mapping helper.
 *
 * Computes tile indices from scene (local) cursor coordinates plus the viewport (camera) offset.
 * Floor division is used to ensure stable mapping near tile boundaries. Negative resulting
 * tile coordinates map to sentinel (-1,-1) allowing callers to represent an "out of map" state.
 *
 * Complexity: O(1). No allocations except the returned record.
 * Thread-safety: stateless & immutable.
 */
public final class HoverMath {
    private HoverMath() {}

    /** Immutable tile coordinate pair. */
    public record TileCoord(int x, int y) {}

    /**
     * Maps cursor + viewport into a tile coordinate.
     * @param sceneX cursor X relative to scene (pixels)
     * @param sceneY cursor Y relative to scene (pixels)
     * @param viewportX horizontal camera offset (pixels)
     * @param viewportY vertical camera offset (pixels)
     * @param tileSize tile dimension in pixels (must be >0)
     * @return TileCoord or (-1,-1) sentinel if result would be negative
     * @throws IllegalArgumentException for tileSize <=0 or NaN inputs
     */
    public static TileCoord mapCursorToTile(double sceneX, double sceneY, double viewportX, double viewportY, int tileSize) {
        if (tileSize <= 0 || anyNaN(sceneX, sceneY, viewportX, viewportY)) {
            throw new IllegalArgumentException("Invalid arguments for mapCursorToTile");
        }
        double worldX = sceneX + viewportX;
        double worldY = sceneY + viewportY;
        int tileX = (int) Math.floor(worldX / tileSize);
        int tileY = (int) Math.floor(worldY / tileSize);
        if (tileX < 0 || tileY < 0) {
            return new TileCoord(-1, -1);
        }
        return new TileCoord(tileX, tileY);
    }

    private static boolean anyNaN(double... v) {
        for (double d : v) if (Double.isNaN(d)) return true; return false;
    }
}
