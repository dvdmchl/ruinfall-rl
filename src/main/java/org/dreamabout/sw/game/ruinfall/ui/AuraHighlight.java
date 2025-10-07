package org.dreamabout.sw.game.ruinfall.ui;

import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * T026: Reusable highlight node drawn under selected object.
 * Pure JavaFX; positioning logic provided via update(tileX,tileY,tileSize).
 */
public class AuraHighlight extends Group {

    private final Circle outer;
    private final Circle inner;
    private int tileSize;

    public AuraHighlight(int tileSize) {
        this.tileSize = tileSize;
        double r = tileSize * 0.45;
        outer = new Circle(r, Color.color(1,1,0,0.15));
        outer.setStroke(Color.color(1,1,0,0.6));
        outer.setStrokeWidth(2);
        inner = new Circle(r * 0.55, Color.color(1,1,0,0.35));
        outer.setEffect(new DropShadow(8, Color.color(1,1,0,0.6)));
        getChildren().addAll(outer, inner);
        setMouseTransparent(true);
        setVisible(false);
    }

    public void updatePosition(int tileX, int tileY) {
        setTranslateX(tileX * tileSize + tileSize / 2.0);
        setTranslateY(tileY * tileSize + tileSize / 2.0);
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }
}
