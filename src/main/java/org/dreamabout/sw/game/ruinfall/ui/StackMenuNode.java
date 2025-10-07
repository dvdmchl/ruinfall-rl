package org.dreamabout.sw.game.ruinfall.ui;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.dreamabout.sw.game.ruinfall.interaction.InteractiveObject;
import org.dreamabout.sw.game.ruinfall.localization.Messages;

import java.util.List;
import java.util.function.Consumer;

/**
 * Simple stack selection menu shown when multiple objects occupy the same tile.
 */
public class StackMenuNode extends Group {

    private static final double PADDING = 6;
    private static final double MARGIN = 4;

    private final VBox box = new VBox(2);
    private final Rectangle bg = new Rectangle();

    private Consumer<InteractiveObject> onSelect;

    public StackMenuNode() {
        box.setPadding(new Insets(PADDING));
        getChildren().addAll(bg, box);
        setVisible(false);
        setMouseTransparent(false);
    }

    public void show(List<InteractiveObject> stack,
                     int tileX,
                     int tileY,
                     int tileSize,
                     double viewportWidth,
                     double viewportHeight,
                     double sidePanelStartX,
                     Consumer<InteractiveObject> onSelect) {
        if (stack == null || stack.isEmpty()) {
            hide();
            return;
        }
        this.onSelect = onSelect;
        box.getChildren().clear();
        Label header = new Label(Messages.get("ui.stackMenu.title"));
        header.setFont(Font.font("Consolas", 14));
        header.setTextFill(Color.color(1, 1, 1, 0.8));
        header.setPadding(new Insets(0, 4, 4, 4));
        box.getChildren().add(header);
        int index = 1;
        for (InteractiveObject obj : stack) {
            Label label = new Label(index + ". " + obj.getDisplayName() + " (" + Messages.get("types." + obj.getType().name().toLowerCase()) + ")");
            label.setFont(Font.font("Consolas", 13));
            label.setTextFill(Color.WHITE);
            label.setPadding(new Insets(2, 4, 2, 4));
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (this.onSelect != null) {
                    this.onSelect.accept(obj);
                }
                hide();
                e.consume();
            });
            box.getChildren().add(label);
            index++;
        }
        layoutBounds();
        place(tileX, tileY, tileSize, viewportWidth, viewportHeight, sidePanelStartX);
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
        box.getChildren().clear();
        onSelect = null;
    }

    private void layoutBounds() {
        applyCss();
        layout();
        double w = box.prefWidth(-1) + PADDING * 2;
        double h = box.prefHeight(-1) + PADDING * 2;
        bg.setWidth(w);
        bg.setHeight(h);
        bg.setArcWidth(8);
        bg.setArcHeight(8);
        bg.setFill(Color.color(0, 0, 0, 0.80));
        bg.setStroke(Color.color(1, 1, 1, 0.25));
        bg.setStrokeWidth(1);
    }

    private void place(int tileX,
                       int tileY,
                       int tileSize,
                       double viewportWidth,
                       double viewportHeight,
                       double sidePanelStartX) {
        layoutBounds();
        double w = bg.getWidth();
        double h = bg.getHeight();
        double centerX = tileX * tileSize + tileSize / 2.0;
        double baseY = tileY * tileSize + tileSize / 2.0;
        double x = centerX + 12;
        if (sidePanelStartX > 0 && x + w > sidePanelStartX - MARGIN) {
            x = centerX - 12 - w;
        }
        if (x < MARGIN) {
            x = MARGIN;
        }
        if (x + w > viewportWidth - MARGIN) {
            x = viewportWidth - w - MARGIN;
        }
        double y = baseY - h - 12;
        if (y < MARGIN) {
            y = baseY + 12;
            if (y + h > viewportHeight - MARGIN) {
                y = viewportHeight - h - MARGIN;
            }
        }
        setTranslateX(x);
        setTranslateY(y);
    }
}
