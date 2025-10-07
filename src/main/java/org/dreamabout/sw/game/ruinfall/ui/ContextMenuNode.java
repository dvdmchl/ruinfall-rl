package org.dreamabout.sw.game.ruinfall.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.dreamabout.sw.game.ruinfall.interaction.ContextMenuModel;
import org.dreamabout.sw.game.ruinfall.interaction.InteractiveActionItem;
import org.dreamabout.sw.game.ruinfall.localization.Messages;

import java.util.function.Consumer;

/**
 * T028: ContextMenuNode listing InteractiveActionItem entries with placement logic.
 */
public class ContextMenuNode extends Group {

    private static final double MARGIN = 4;
    private static final double ITEM_W = 160;
    private static final double PADDING = 6;

    private final VBox box = new VBox(4);
    private final Rectangle bg = new Rectangle();

    private Consumer<String> onAction; // action code consumer

    public ContextMenuNode() {
        box.setPadding(new Insets(PADDING));
        box.setAlignment(Pos.TOP_LEFT);
        getChildren().addAll(bg, box);
        setVisible(false);
        setMouseTransparent(false);
    }

    public void setOnAction(Consumer<String> onAction) { this.onAction = onAction; }

    public void setModel(ContextMenuModel model) {
        box.getChildren().clear();
        if (model == null) { setVisible(false); return; }
        setVisible(true);
        for (InteractiveActionItem item : model.actions()) {
            Label l = new Label(Messages.get(item.labelKey()));
            l.setFont(Font.font("Consolas", 14));
            l.setTextFill(item.enabled() ? Color.WHITE : Color.color(1,1,1,0.35));
            l.setDisable(!item.enabled());
            l.setMinWidth(ITEM_W);
            l.setPrefWidth(ITEM_W);
            l.setPadding(new Insets(2,4,2,4));
            l.getStyleClass().add("context-menu-item");
            l.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (item.enabled() && onAction != null) onAction.accept(item.code());
                e.consume();
            });
            box.getChildren().add(l);
        }
        layoutBounds();
    }

    private void layoutBounds() {
        applyCss(); layout();
        double w = ITEM_W + PADDING * 2;
        double h = box.prefHeight(ITEM_W) + PADDING * 2;
        bg.setWidth(w);
        bg.setHeight(h);
        bg.setArcWidth(8);
        bg.setArcHeight(8);
        bg.setFill(Color.color(0,0,0,0.85));
        bg.setStroke(Color.color(1,1,1,0.25));
        bg.setStrokeWidth(1);
    }

    /**
     * Placement rules: prefer right side of anchor (anchorX + 8). Fallback left. Vertical prefer above then below.
     * Side panel startX used to avoid overlap.
     */
    public void place(double anchorX, double anchorY, double viewportWidth, double viewportHeight, double sidePanelStartX) {
        layoutBounds();
        double w = bg.getWidth();
        double h = bg.getHeight();
        double[] pos = computePlacement(anchorX, anchorY, viewportWidth, viewportHeight, sidePanelStartX, w, h);
        setTranslateX(pos[0]);
        setTranslateY(pos[1]);
    }

    public static double[] computePlacement(double anchorX,
                                             double anchorY,
                                             double viewportWidth,
                                             double viewportHeight,
                                             double sidePanelStartX,
                                             double menuWidth,
                                             double menuHeight) {
        double x = anchorX + 8;
        if (sidePanelStartX > 0 && x + menuWidth > sidePanelStartX - MARGIN) {
            x = anchorX - 8 - menuWidth;
        }
        x = clamp(x, MARGIN, viewportWidth - menuWidth - MARGIN);
        double y = anchorY - menuHeight - 12;
        if (y < MARGIN) {
            y = anchorY + 12;
            if (y + menuHeight > viewportHeight - MARGIN) {
                y = viewportHeight - menuHeight - MARGIN;
            }
        }
        return new double[]{x, y};
    }

    private static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
