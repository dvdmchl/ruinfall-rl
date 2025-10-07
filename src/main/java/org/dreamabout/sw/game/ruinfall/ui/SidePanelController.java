package org.dreamabout.sw.game.ruinfall.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.dreamabout.sw.game.ruinfall.interaction.SidePanelViewModel;
import org.dreamabout.sw.game.ruinfall.localization.Messages;

/**
 * T029: Side panel binds to current selection producing simple textual representation.
 */
public class SidePanelController extends VBox {

    private final Label header = new Label();
    private final Label type = new Label();
    private final Label desc = new Label();
    private final VBox actionsBox = new VBox(2);

    public SidePanelController() {
        setPadding(new Insets(8));
        setSpacing(8);
        header.setTextFill(Color.WHITE);
        header.setFont(Font.font("Consolas", 18));
        type.setTextFill(Color.color(1,1,1,0.75));
        type.setFont(Font.font("Consolas", 12));
        desc.setTextFill(Color.color(1,1,1,0.85));
        desc.setWrapText(true);
        getChildren().addAll(header, type, desc, actionsBox);
        setStyle("-fx-background-color: rgba(10,10,12,0.95);");
        showNothingSelected();
    }

    private void showNothingSelected() {
        header.setText(Messages.get("ui.sidePanel.headerPlaceholder"));
        type.setText("");
        desc.setText(Messages.get("ui.sidePanel.nothingSelected"));
        actionsBox.getChildren().clear();
    }

    public void update(SidePanelViewModel vm) {
        if (vm == null || vm.selectedObjectId() == null) {
            showNothingSelected();
            return;
        }
        header.setText(vm.headerName());
        String typeKey = vm.typeLabel() == null ? null : "types." + vm.typeLabel().toLowerCase();
        type.setText(typeKey == null ? "" : Messages.get(typeKey));
        desc.setText(vm.description());
        actionsBox.getChildren().clear();
        vm.actions().forEach(a -> {
            var l = new Label(Messages.get(a.labelKey()));
            l.setFont(Font.font("Consolas", 12));
            l.setTextFill(a.enabled()? Color.WHITE : Color.color(1,1,1,0.35));
            actionsBox.getChildren().add(l);
        });
    }
}