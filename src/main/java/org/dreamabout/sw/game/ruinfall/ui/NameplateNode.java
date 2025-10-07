package org.dreamabout.sw.game.ruinfall.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.dreamabout.sw.game.ruinfall.interaction.NameplateModel;

import java.util.List;

/**
 * T027: Displays name + summary lines. Width clamp 120-240 px.
 * Provides simple ellipsis for overlong single word lines.
 */
public class NameplateNode extends Group {

    private static final double MIN_W = 120;
    private static final double MAX_W = 240;
    private static final double PADDING = 6;
    private static final double MARGIN = 4;

    private final VBox box = new VBox(2);
    private final Rectangle bg = new Rectangle();

    public NameplateNode() {
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(PADDING));
        box.setBackground(Background.EMPTY);
        getChildren().addAll(bg, box);
        setMouseTransparent(true);
    }

    public void setModel(NameplateModel model) {
        box.getChildren().clear();
        if (model == null) { setVisible(false); return; }
        setVisible(true);
        List<String> lines = model.lines();
        for (String line : lines) {
            box.getChildren().add(makeLabel(ellipsisIfNeeded(line)));
        }
        layoutBounds();
    }

    private Label makeLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.WHITE);
        l.setFont(Font.font("Consolas", 14));
        l.setWrapText(true);
        l.setTextAlignment(TextAlignment.LEFT);
        return l;
    }

    private String ellipsisIfNeeded(String s) {
        // Approximate width measurement by character count; if too long, truncate.
        if (s.length() > 48) {
            return s.substring(0, 45) + "…";
        }
        return s;
    }

    private void layoutBounds() {
        applyCss();
        layout();
        double prefW = box.prefWidth(-1);
        // Clamp width
        double w = Math.max(MIN_W, Math.min(MAX_W, prefW + PADDING * 2));
        box.setPrefWidth(w - PADDING * 2);
        for (var n : box.getChildren()) {
            if (n instanceof Label l) {
                l.setMaxWidth(w - PADDING * 2);
                l.setPrefWidth(w - PADDING * 2);
            }
        }
        box.applyCss();
        box.layout();
        double h = box.prefHeight(w - PADDING * 2) + PADDING * 2;
        bg.setWidth(w);
        bg.setHeight(h);
        bg.setArcWidth(8);
        bg.setArcHeight(8);
        bg.setFill(Color.color(0,0,0,0.70));
        bg.setStroke(Color.color(1,1,1,0.25));
        bg.setStrokeWidth(1.0);
    }

    /**
     * Place near tile center. Prefer above (offset -12). If clips top, place below.
     * Clamp horizontally inside viewport and away from side panel (if provided >0).
     */
    public void place(int tileX, int tileY, int tileSize, double viewportWidth, double viewportHeight, double sidePanelStartX) {
        layoutBounds();
        double w = bg.getWidth();
        double h = bg.getHeight();
        double centerX = tileX * tileSize + tileSize / 2.0;
        double centerY = tileY * tileSize + tileSize / 2.0;
        double x = centerX - w / 2.0;
        if (sidePanelStartX > 0 && x + w > sidePanelStartX - MARGIN) {
            x = sidePanelStartX - w - MARGIN;
        }
        if (x < MARGIN) x = MARGIN;
        double y = centerY - tileSize - h / 2.0 - 4; // above
        if (y < MARGIN) {
            y = centerY + tileSize / 2.0 + 4;
            if (y + h > viewportHeight - MARGIN) y = viewportHeight - h - MARGIN;
        }
        setTranslateX(x);
        setTranslateY(y);
    }
}
