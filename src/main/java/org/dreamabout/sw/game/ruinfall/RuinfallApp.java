package org.dreamabout.sw.game.ruinfall;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;
import javafx.scene.Node;
import org.dreamabout.sw.game.ruinfall.model.*;
import org.dreamabout.sw.game.ruinfall.system.*;
import org.dreamabout.sw.game.ruinfall.ui.HUDController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Minimal FXGL application scaffold for Ruinfall.
 * NOTE: Core gameplay systems (generation, LOS, etc.) intentionally absent until tasks are executed.
 */
public class RuinfallApp extends GameApplication {
    private static final int TILE_SIZE = 16;
    private long seed;
    private Dungeon dungeon;
    private Player player;
    private Enemy enemy;
    private final DungeonGenerator generator = new DungeonGenerator();
    private final VisibilitySystem visibilitySystem = new VisibilitySystem();
    private final EnemyAI enemyAI = new EnemyAI();
    private final DamageSystem damageSystem = new DamageSystem();
    private final TurnSystem turnSystem = new TurnSystem();
    private final HUDController hud = new HUDController();
    private final RestartService restartService = new RestartService();
    private final Random rng = new Random();
    private static final long MOVE_DELAY_NANOS = 120_000_000L; // 120 ms between moves when holding
    private final AtomicLong lastMoveTime = new AtomicLong(0L);
    private boolean deterministicSeed = false;

    private Entity playerEntity; private Entity enemyEntity; private javafx.scene.shape.Rectangle[][] tileRects;
    private Text seedText;
    private Node sidePanel; // UI panel filling unused horizontal space
    private int dungeonWidthTiles = 64; // keep width
    private int dungeonHeightTiles;     // computed to fit window

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Ruinfall (Minimal Core)");
        settings.setVersion("0.0.1");
        settings.setTicksPerSecond(60);
    }

    @Override
    protected void initGame() {
        parseSeed();
        generateAndInit();
    }

    private void parseSeed(){
        Long sys = Long.getLong("ruinfall.seed", null);
        if(sys!=null){ seed = sys; deterministicSeed = true; } else { seed = System.currentTimeMillis(); deterministicSeed = false; }
    }

    private void generateAndInit(){
        getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);
        // Remove previous side panel & seed text
        if (sidePanel != null) FXGL.getGameScene().removeUINode(sidePanel);
        if (seedText != null) FXGL.getGameScene().removeUINode(seedText);
        turnSystem.reset();
        int visibleTilesY = FXGL.getAppHeight() / TILE_SIZE;
        dungeonHeightTiles = Math.min(48, Math.max(visibleTilesY - 1, 10));
        // Keep map width capped ( design cap 64 ); we fill remaining area with side panel intentionally
        dungeonWidthTiles = 64;
        dungeon = generator.generate(seed, dungeonWidthTiles, dungeonHeightTiles);
        player = new Player(dungeon.getPlayerSpawnX(), dungeon.getPlayerSpawnY(),5);
        enemy = new Enemy(dungeon.getEnemySpawnX(), dungeon.getEnemySpawnY());
        visibilitySystem.recomputeVisibility(dungeon, player.getX(), player.getY(), 8);
        buildTileEntities();
        spawnActors();
        configureViewport();
        hud.initHUD(player);
        createOrUpdateSidePanel();
        seedText = buildSeedText();
        addUINode(seedText);
        positionSeedText();
        refreshTileVisibility();
        updateActorVisibility();
    }
    private Text buildSeedText(){
        Text t = new Text("Seed: "+seed+" ("+dungeonWidthTiles+"x"+dungeonHeightTiles+")");
        t.setFill(Color.WHITE);
        t.setEffect(new DropShadow(4, Color.color(0,0,0,0.85)));
        t.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return t;
    }
    private void positionSeedText(){
        if (seedText == null) return;
        // If we have a side panel, place inside panel with padding; else top-left over map with small offset
        int mapPixelWidth = dungeonWidthTiles * TILE_SIZE;
        if (sidePanel != null) {
            seedText.setTranslateX(mapPixelWidth + 16);
            seedText.setTranslateY(32);
        } else {
            seedText.setTranslateX(10);
            seedText.setTranslateY(24);
        }
    }
    private void createOrUpdateSidePanel(){
        int mapPixelWidth = dungeonWidthTiles * TILE_SIZE;
        int totalWidth = FXGL.getAppWidth();
        int panelWidth = totalWidth - mapPixelWidth;
        if (panelWidth <= 0) { sidePanel = null; return; }
        Rectangle panel = new Rectangle(panelWidth, FXGL.getAppHeight());
        panel.setFill(Color.color(0.07,0.07,0.09));
        panel.setTranslateX(mapPixelWidth);
        panel.setTranslateY(0);
        sidePanel = panel;
        addUINode(sidePanel);
    }
    private void configureViewport(){
        var vp = FXGL.getGameScene().getViewport();
        vp.setBounds(0,0, dungeonWidthTiles * TILE_SIZE, dungeonHeightTiles * TILE_SIZE);
        boolean taller = dungeonHeightTiles * TILE_SIZE > FXGL.getAppHeight();
        boolean wider = dungeonWidthTiles * TILE_SIZE > FXGL.getAppWidth();
        if(taller || wider) {
            vp.bindToEntity(playerEntity, FXGL.getAppWidth()/2.0, FXGL.getAppHeight()/2.0);
        } else {
            // Clear camera binding (FXGL 21.1: use unbind, not clearEntity)
            vp.unbind();
            vp.setX(0);
            vp.setY(0);
        }
    }
    private void buildTileEntities(){
        // Clear existing tile entities if regenerating
        getGameWorld().getEntitiesCopy().stream().filter(e -> e.hasComponent(com.almasb.fxgl.entity.component.Component.class));
        tileRects = new javafx.scene.shape.Rectangle[dungeon.getHeight()][dungeon.getWidth()];
        for(int y=0;y<dungeon.getHeight();y++){
            for(int x=0;x<dungeon.getWidth();x++){
                javafx.scene.shape.Rectangle r = new javafx.scene.shape.Rectangle(TILE_SIZE, TILE_SIZE);
                tileRects[y][x]=r;
                updateTileColor(x,y);
                entityBuilder().at(x*TILE_SIZE, y*TILE_SIZE).view(r).type(EntityType.TILE).buildAndAttach();
            }
        }
    }
    private void updateTileColor(int x,int y){
        Tile t = dungeon.getTile(x,y);
        javafx.scene.shape.Rectangle r = tileRects[y][x];
        switch(t.getVisibility()){
            case VISIBLE -> r.setFill(t.getType()== TileType.WALL? Color.DARKSLATEGRAY: Color.DIMGRAY);
            case MEMORY -> r.setFill(t.getType()== TileType.WALL? Color.color(0.12,0.12,0.15): Color.color(0.25,0.25,0.25));
            case UNSEEN -> r.setFill(Color.BLACK);
        }
    }
    private void refreshTileVisibility(){
        for(int y=0;y<dungeon.getHeight();y++) for(int x=0;x<dungeon.getWidth();x++) updateTileColor(x,y);
        positionSeedText(); // reposition seed text if needed (window resize not covered yet but safe)
    }
    private void spawnActors(){
        playerEntity = entityBuilder().at(player.getX()*TILE_SIZE, player.getY()*TILE_SIZE)
                .view(new Rectangle(TILE_SIZE, TILE_SIZE, Color.YELLOW)).type(EntityType.PLAYER).buildAndAttach();
        enemyEntity = entityBuilder().at(enemy.getX()*TILE_SIZE, enemy.getY()*TILE_SIZE)
                .view(new Rectangle(TILE_SIZE, TILE_SIZE, Color.RED)).type(EntityType.ENEMY).buildAndAttach();
    }
    private void updateEntityPositions(){
        playerEntity.setX(player.getX()*TILE_SIZE);
        playerEntity.setY(player.getY()*TILE_SIZE);
        enemyEntity.setX(enemy.getX()*TILE_SIZE);
        enemyEntity.setY(enemy.getY()*TILE_SIZE);
    }
    private void updateActorVisibility(){
        if(enemyEntity!=null){
            boolean enemyVisible = dungeon.getTile(enemy.getX(), enemy.getY()).getVisibility()==VisibilityState.VISIBLE;
            enemyEntity.getViewComponent().setVisible(enemyVisible);
        }
        if(playerEntity!=null){
            playerEntity.getViewComponent().setVisible(true); // player always visible at its tile
        }
    }
    private void movePlayer(int dx,int dy){
        long now = System.nanoTime();
        if(now - lastMoveTime.get() < MOVE_DELAY_NANOS) return; // throttle
        if(turnSystem.isRunEnded()) return;
        int nx = player.getX()+dx; int ny = player.getY()+dy;
        if(nx>=0&&ny>=0&&nx<dungeon.getWidth()&&ny<dungeon.getHeight()&&dungeon.getTile(nx,ny).getType()==TileType.FLOOR){
            player.setPosition(nx,ny);
            turnSystem.nextTurn();
            // Damage if starting overlap (player moved onto enemy)
            int hpBefore = player.getHp();
            damageSystem.applyContactDamage(player, enemy, turnSystem);
            if(player.getHp() < hpBefore) System.out.println("[DAMAGE] Player HP="+player.getHp()+" turn="+turnSystem.getCurrentTurn());
            // Enemy moves
            enemyAI.moveEnemy(dungeon, enemy, rng);
            // Damage if enemy moved onto player
            hpBefore = player.getHp();
            damageSystem.applyContactDamage(player, enemy, turnSystem);
            if(player.getHp() < hpBefore) System.out.println("[DAMAGE] Player HP="+player.getHp()+" turn="+turnSystem.getCurrentTurn());
            if(player.getHp()==0){ turnSystem.endRun(); hud.showGameOver(); }
            visibilitySystem.recomputeVisibility(dungeon, player.getX(), player.getY(), 8);
            refreshTileVisibility();
            updateEntityPositions();
            updateActorVisibility();
            hud.updateHP(player);
            lastMoveTime.set(now);
        }
    }
    @Override
    protected void initInput() {
        // Repeat while key held using onKey (fires each frame); throttling handled in movePlayer
        FXGL.onKey(KeyCode.UP, () -> movePlayer(0,-1));
        FXGL.onKey(KeyCode.DOWN, () -> movePlayer(0,1));
        FXGL.onKey(KeyCode.LEFT, () -> movePlayer(-1,0));
        FXGL.onKey(KeyCode.RIGHT, () -> movePlayer(1,0));
        FXGL.onKey(KeyCode.W, () -> movePlayer(0,-1));
        FXGL.onKey(KeyCode.S, () -> movePlayer(0,1));
        FXGL.onKey(KeyCode.A, () -> movePlayer(-1,0));
        FXGL.onKey(KeyCode.D, () -> movePlayer(1,0));
        FXGL.onKeyDown(KeyCode.R, this::restartRun);
        FXGL.onKeyDown(KeyCode.ESCAPE, () -> FXGL.getGameController().exit());
    }
    private void restartRun(){
        if(!deterministicSeed) {
            seed = System.currentTimeMillis();
        }
        hud.hideGameOver();
        generateAndInit();
    }

    public static void main(String[] args) { launch(args); }
}
