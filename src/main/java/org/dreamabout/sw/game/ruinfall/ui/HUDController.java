package org.dreamabout.sw.game.ruinfall.ui;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.dreamabout.sw.game.ruinfall.model.Player;
public class HUDController {
    private Text hpText;
    private Text overlay;
    public void initHUD(Player p) {
        if (hpText == null) {
            hpText = new Text();
            hpText.setFont(Font.font(16));
            hpText.setFill(Color.WHITE);
            hpText.setTranslateX(8);
            hpText.setTranslateY(32);
            FXGL.addUINode(hpText);
        }
        if (overlay == null) {
            overlay = new Text("YOU DIED - Press R to Restart");
            overlay.setFont(Font.font(32));
            overlay.setFill(Color.RED);
            overlay.setTranslateX(200);
            overlay.setTranslateY(200);
            overlay.setVisible(false);
            FXGL.addUINode(overlay);
        }
        updateHP(p);
    }
    public void updateHP(Player p) {
        if (hpText != null) {
            hpText.setText("HP: " + p.getHp() + "/" + p.getMaxHp());
        }
    }
    public void showGameOver() {
        if (overlay != null) overlay.setVisible(true);
    }
    public void hideGameOver() {
        if (overlay != null) overlay.setVisible(false);
    }
}
