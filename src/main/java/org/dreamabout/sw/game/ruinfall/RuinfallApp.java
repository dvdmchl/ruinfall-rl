package org.dreamabout.sw.game.ruinfall;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.dreamabout.sw.game.ruinfall.interaction.*;
import org.dreamabout.sw.game.ruinfall.localization.Messages;
import org.dreamabout.sw.game.ruinfall.model.*;
import org.dreamabout.sw.game.ruinfall.system.*;
import org.dreamabout.sw.game.ruinfall.ui.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.almasb.fxgl.dsl.FXGL.addUINode;

/**
 * Minimal FXGL application scaffold extended with the interaction system.
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
    private static final long MOVE_DELAY_NANOS = 120_000_000L;
    private final AtomicLong lastMoveTime = new AtomicLong(0L);
    private boolean deterministicSeed = false;

    private Entity playerEntity;
    private Entity enemyEntity;
    private Entity chestEntity;
    private Entity npcEntity;
    private Rectangle[][] tileRects;
    private Text seedText;
    private Node sidePanelNode;
    private double sidePanelStartX;

    private int dungeonWidthTiles = 64;
    private int dungeonHeightTiles;

    private InteractiveRegistry interactiveRegistry;
    private SelectionManager selectionManager;
    private HoverManager hoverManager;
    private InteractionUIController interactionController;

    private EnemyObjectAdapter enemyInteractive;
    private Chest chestInteractive;
    private NPC npcInteractive;

    private AuraHighlight auraHighlight;
    private NameplateNode nameplateNode;
    private ContextMenuNode contextMenuNode;
    private SidePanelController sidePanelController;
    private StackMenuNode stackMenuNode;

    private boolean interactionNodesAttached = false;

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

    private void parseSeed() {
        Long sys = Long.getLong("ruinfall.seed", null);
        if (sys != null) {
            seed = sys;
            deterministicSeed = true;
        } else {
            seed = System.currentTimeMillis();
            deterministicSeed = false;
        }
    }

    private void generateAndInit() {
        FXGL.getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);
        if (sidePanelNode != null) {
            FXGL.getGameScene().removeUINode(sidePanelNode);
            sidePanelNode = null;
        }
        if (seedText != null) {
            FXGL.getGameScene().removeUINode(seedText);
            seedText = null;
        }

        turnSystem.reset();
        int visibleTilesY = FXGL.getAppHeight() / TILE_SIZE;
        dungeonHeightTiles = Math.min(48, Math.max(visibleTilesY - 1, 10));
        dungeonWidthTiles = 64;

        dungeon = generator.generate(seed, dungeonWidthTiles, dungeonHeightTiles);
        player = new Player(dungeon.getPlayerSpawnX(), dungeon.getPlayerSpawnY(), 5);
        enemy = new Enemy(dungeon.getEnemySpawnX(), dungeon.getEnemySpawnY());

        setupInteractionSystems();

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
        refreshInteractionUI();
    }

    private void setupInteractionSystems() {
        interactiveRegistry = new InteractiveRegistry();
        selectionManager = new SelectionManager();
        hoverManager = new HoverManager();
        interactionController = new InteractionUIController(interactiveRegistry, selectionManager, hoverManager);
        registerInteractiveObjects();
        hoverManager.onHoverTile(-1, -1, interactiveRegistry);
        selectionManager.clear();
        interactionController.closeContextMenu();
    }

    private void ensureInteractionNodesAttached() {
        if (auraHighlight == null) {
            auraHighlight = new AuraHighlight(TILE_SIZE);
        }
        if (nameplateNode == null) {
            nameplateNode = new NameplateNode();
        }
        if (contextMenuNode == null) {
            contextMenuNode = new ContextMenuNode();
        }
        if (stackMenuNode == null) {
            stackMenuNode = new StackMenuNode();
        }
        if (sidePanelController == null) {
            sidePanelController = new SidePanelController();
        }
        auraHighlight.setVisible(false);
        nameplateNode.setModel(null);
        contextMenuNode.setModel(null);
        stackMenuNode.setVisible(false);
        if (!interactionNodesAttached) {
            addUINode(auraHighlight);
            addUINode(nameplateNode);
            addUINode(contextMenuNode);
            addUINode(stackMenuNode);
            contextMenuNode.setOnAction(this::handleActionInvoked);
            interactionNodesAttached = true;
        }
    }

    private void registerInteractiveObjects() {
        enemyInteractive = new EnemyObjectAdapter(
                "enemy-" + dungeon.getSeed(),
                "Stalking Shade",
                "A hostile guardian of the ruins.",
                enemy.getX(),
                enemy.getY());
        interactiveRegistry.register(enemyInteractive);

        Set<Long> reserved = new HashSet<>();
        reserved.add(tileKey(player.getX(), player.getY()));
        reserved.add(tileKey(enemy.getX(), enemy.getY()));

        int[] chestPos = findNearestFloor(player.getX(), player.getY(), reserved);
        chestInteractive = new Chest(
                "chest-" + dungeon.getSeed(),
                "Cracked Supply Chest",
                "An old chest that rattles when nudged.",
                chestPos[0],
                chestPos[1]);
        interactiveRegistry.register(chestInteractive);
        reserved.add(tileKey(chestPos[0], chestPos[1]));

        int[] npcPos = findNearestFloor(enemy.getX(), enemy.getY(), reserved);
        npcInteractive = new NPC(
                "npc-" + dungeon.getSeed(),
                "Weary Scout",
                "Looks eager to share rumors about the catacombs.",
                npcPos[0],
                npcPos[1]);
        interactiveRegistry.register(npcInteractive);
        reserved.add(tileKey(npcPos[0], npcPos[1]));
    }

    private int[] findNearestFloor(int originX, int originY, Set<Long> reserved) {
        int maxRadius = Math.max(dungeon.getWidth(), dungeon.getHeight());
        for (int radius = 1; radius <= maxRadius; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    if (Math.max(Math.abs(dx), Math.abs(dy)) != radius) {
                        continue;
                    }
                    int x = originX + dx;
                    int y = originY + dy;
                    if (!inBounds(x, y)) {
                        continue;
                    }
                    if (dungeon.getTile(x, y).getType() != TileType.FLOOR) {
                        continue;
                    }
                    long key = tileKey(x, y);
                    if (reserved.contains(key)) {
                        continue;
                    }
                    return new int[]{x, y};
                }
            }
        }
        return new int[]{originX, originY};
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < dungeon.getWidth() && y < dungeon.getHeight();
    }

    private long tileKey(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }

    private Text buildSeedText() {
        Text t = new Text("Seed: " + seed + " (" + dungeonWidthTiles + "x" + dungeonHeightTiles + ")");
        t.setFill(Color.WHITE);
        t.setEffect(new DropShadow(4, Color.color(0, 0, 0, 0.85)));
        t.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return t;
    }

    private void positionSeedText() {
        if (seedText == null) {
            return;
        }
        int mapPixelWidth = dungeonWidthTiles * TILE_SIZE;
        if (sidePanelNode != null) {
            seedText.setTranslateX(mapPixelWidth + 16);
            seedText.setTranslateY(32);
        } else {
            seedText.setTranslateX(10);
            seedText.setTranslateY(24);
        }
    }

    private void createOrUpdateSidePanel() {
        int mapPixelWidth = dungeonWidthTiles * TILE_SIZE;
        sidePanelStartX = mapPixelWidth;
        int totalWidth = FXGL.getAppWidth();
        int panelWidth = totalWidth - mapPixelWidth;
        if (panelWidth <= 0) {
            sidePanelNode = null;
            return;
        }
        sidePanelController.setPrefWidth(panelWidth);
        sidePanelController.setMaxWidth(panelWidth);
        sidePanelController.setPrefHeight(FXGL.getAppHeight());
        StackPane container = new StackPane(sidePanelController);
        container.setPrefSize(panelWidth, FXGL.getAppHeight());
        container.setTranslateX(mapPixelWidth);
        container.setTranslateY(0);
        container.setStyle("-fx-background-color: rgba(10,10,12,0.95); -fx-border-color: rgba(255,255,255,0.12); -fx-border-width: 0 0 0 1;");
        if (sidePanelNode != null) {
            FXGL.getGameScene().removeUINode(sidePanelNode);
        }
        sidePanelNode = container;
        addUINode(sidePanelNode);
    }

    private void configureViewport() {
        var vp = FXGL.getGameScene().getViewport();
        vp.setBounds(0, 0, dungeonWidthTiles * TILE_SIZE, dungeonHeightTiles * TILE_SIZE);
        boolean taller = dungeonHeightTiles * TILE_SIZE > FXGL.getAppHeight();
        boolean wider = dungeonWidthTiles * TILE_SIZE > FXGL.getAppWidth();
        if (taller || wider) {
            vp.bindToEntity(playerEntity, FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0);
        } else {
            vp.unbind();
            vp.setX(0);
            vp.setY(0);
        }
    }

    private void buildTileEntities() {
        tileRects = new Rectangle[dungeon.getHeight()][dungeon.getWidth()];
        for (int y = 0; y < dungeon.getHeight(); y++) {
            for (int x = 0; x < dungeon.getWidth(); x++) {
                Rectangle r = new Rectangle(TILE_SIZE, TILE_SIZE);
                tileRects[y][x] = r;
                updateTileColor(x, y);
                FXGL.entityBuilder()
                        .at(x * TILE_SIZE, y * TILE_SIZE)
                        .view(r)
                        .type(EntityType.TILE)
                        .buildAndAttach();
            }
        }
    }

    private void updateTileColor(int x, int y) {
        Tile t = dungeon.getTile(x, y);
        Rectangle r = tileRects[y][x];
        switch (t.getVisibility()) {
            case VISIBLE -> r.setFill(t.getType() == TileType.WALL ? Color.DARKSLATEGRAY : Color.DIMGRAY);
            case MEMORY -> r.setFill(t.getType() == TileType.WALL ? Color.color(0.12, 0.12, 0.15) : Color.color(0.25, 0.25, 0.25));
            case UNSEEN -> r.setFill(Color.BLACK);
        }
    }

    private void refreshTileVisibility() {
        for (int y = 0; y < dungeon.getHeight(); y++) {
            for (int x = 0; x < dungeon.getWidth(); x++) {
                updateTileColor(x, y);
            }
        }
        positionSeedText();
    }

    private void spawnActors() {
        playerEntity = FXGL.entityBuilder()
                .at(player.getX() * TILE_SIZE, player.getY() * TILE_SIZE)
                .view(new Rectangle(TILE_SIZE, TILE_SIZE, Color.YELLOW))
                .type(EntityType.PLAYER)
                .buildAndAttach();
        enemyEntity = FXGL.entityBuilder()
                .at(enemy.getX() * TILE_SIZE, enemy.getY() * TILE_SIZE)
                .view(new Rectangle(TILE_SIZE, TILE_SIZE, Color.RED))
                .type(EntityType.ENEMY)
                .buildAndAttach();
        if (chestInteractive != null) {
            chestEntity = FXGL.entityBuilder()
                    .at(chestInteractive.getTileX() * TILE_SIZE, chestInteractive.getTileY() * TILE_SIZE)
                    .view(new Rectangle(TILE_SIZE, TILE_SIZE, Color.SADDLEBROWN))
                    .type(EntityType.CHEST)
                    .buildAndAttach();
        }
        if (npcInteractive != null) {
            npcEntity = FXGL.entityBuilder()
                    .at(npcInteractive.getTileX() * TILE_SIZE, npcInteractive.getTileY() * TILE_SIZE)
                    .view(new Rectangle(TILE_SIZE, TILE_SIZE, Color.LIGHTGREEN))
                    .type(EntityType.NPC)
                    .buildAndAttach();
        }
        ensureInteractionNodesAttached();
        if (auraHighlight == null) {
            auraHighlight = new AuraHighlight(TILE_SIZE);
            if (!interactionNodesAttached) {
                addUINode(auraHighlight);
            }
        }
        auraHighlight.setVisible(false);
    }

    private void updateEntityPositions() {
        playerEntity.setX(player.getX() * TILE_SIZE);
        playerEntity.setY(player.getY() * TILE_SIZE);
        enemyEntity.setX(enemy.getX() * TILE_SIZE);
        enemyEntity.setY(enemy.getY() * TILE_SIZE);
        if (chestEntity != null && chestInteractive != null) {
            chestEntity.setX(chestInteractive.getTileX() * TILE_SIZE);
            chestEntity.setY(chestInteractive.getTileY() * TILE_SIZE);
        }
        if (npcEntity != null && npcInteractive != null) {
            npcEntity.setX(npcInteractive.getTileX() * TILE_SIZE);
            npcEntity.setY(npcInteractive.getTileY() * TILE_SIZE);
        }
    }

    private void updateActorVisibility() {
        if (enemyEntity != null) {
            enemyEntity.getViewComponent().setVisible(isInteractiveVisible(enemyInteractive.getId()));
        }
        if (chestEntity != null && chestInteractive != null) {
            chestEntity.getViewComponent().setVisible(isInteractiveVisible(chestInteractive.getId()));
        }
        if (npcEntity != null && npcInteractive != null) {
            npcEntity.getViewComponent().setVisible(isInteractiveVisible(npcInteractive.getId()));
        }
        if (playerEntity != null) {
            playerEntity.getViewComponent().setVisible(true);
        }
    }

    private void movePlayer(int dx, int dy) {
        long now = System.nanoTime();
        if (now - lastMoveTime.get() < MOVE_DELAY_NANOS) {
            return;
        }
        if (turnSystem.isRunEnded()) {
            return;
        }
        int nx = player.getX() + dx;
        int ny = player.getY() + dy;
        if (!inBounds(nx, ny) || dungeon.getTile(nx, ny).getType() != TileType.FLOOR) {
            return;
        }
        player.setPosition(nx, ny);
        turnSystem.nextTurn();
        int hpBefore = player.getHp();
        damageSystem.applyContactDamage(player, enemy, turnSystem);
        if (player.getHp() < hpBefore) {
            System.out.println("[DAMAGE] Player HP=" + player.getHp() + " turn=" + turnSystem.getCurrentTurn());
        }
        enemyAI.moveEnemy(dungeon, enemy, rng);
        hpBefore = player.getHp();
        damageSystem.applyContactDamage(player, enemy, turnSystem);
        if (player.getHp() < hpBefore) {
            System.out.println("[DAMAGE] Player HP=" + player.getHp() + " turn=" + turnSystem.getCurrentTurn());
        }
        if (player.getHp() == 0) {
            turnSystem.endRun();
            hud.showGameOver();
        }
        enemyInteractive.setTilePosition(enemy.getX(), enemy.getY());
        interactiveRegistry.moveObject(enemyInteractive.getId(), enemy.getX(), enemy.getY());
        visibilitySystem.recomputeVisibility(dungeon, player.getX(), player.getY(), 8);
        refreshTileVisibility();
        updateEntityPositions();
        updateActorVisibility();
        hud.updateHP(player);
        lastMoveTime.set(now);
        refreshInteractionUI();
    }

    private boolean isInteractiveVisible(String objectId) {
        if (interactiveRegistry == null) {
            return false;
        }
        InteractiveObject obj = interactiveRegistry.getById(objectId);
        if (obj == null) {
            return false;
        }
        if (!inBounds(obj.getTileX(), obj.getTileY())) {
            return false;
        }
        return dungeon.getTile(obj.getTileX(), obj.getTileY()).getVisibility() == VisibilityState.VISIBLE;
    }

    private void refreshInteractionUI() {
        if (interactionController == null) {
            return;
        }
        if (nameplateNode == null || contextMenuNode == null || sidePanelController == null || auraHighlight == null) {
            return;
        }
        interactionController.validateVisibility(this::isInteractiveVisible);
        var nameplateModel = interactionController.getNameplateModel();
        nameplateNode.setModel(nameplateModel);
        if (nameplateModel != null) {
            InteractiveObject obj = interactiveRegistry.getById(nameplateModel.objectId());
            if (obj != null) {
                nameplateNode.place(obj.getTileX(), obj.getTileY(), TILE_SIZE, FXGL.getAppWidth(), FXGL.getAppHeight(), sidePanelStartX);
            }
        }
        var sidePanelViewModel = interactionController.getSidePanelViewModel();
        sidePanelController.update(sidePanelViewModel);
        var contextModel = interactionController.getContextMenuModel();
        contextMenuNode.setModel(contextModel);
        if (contextModel != null) {
            InteractiveObject obj = interactiveRegistry.getById(contextModel.objectId());
            if (obj != null) {
                double anchorX = obj.getTileX() * TILE_SIZE + TILE_SIZE / 2.0;
                double anchorY = obj.getTileY() * TILE_SIZE + TILE_SIZE / 2.0;
                contextMenuNode.place(anchorX, anchorY, FXGL.getAppWidth(), FXGL.getAppHeight(), sidePanelStartX);
            }
        }
        String selectedId = selectionManager.getState().selectedId();
        if (selectedId != null) {
            InteractiveObject selectedObj = interactiveRegistry.getById(selectedId);
            if (selectedObj != null) {
                auraHighlight.setVisible(true);
                auraHighlight.updatePosition(selectedObj.getTileX(), selectedObj.getTileY());
            } else {
                auraHighlight.setVisible(false);
            }
        } else {
            auraHighlight.setVisible(false);
        }
    }

    @Override
    protected void initInput() {
        FXGL.onKey(KeyCode.UP, () -> movePlayer(0, -1));
        FXGL.onKey(KeyCode.DOWN, () -> movePlayer(0, 1));
        FXGL.onKey(KeyCode.LEFT, () -> movePlayer(-1, 0));
        FXGL.onKey(KeyCode.RIGHT, () -> movePlayer(1, 0));
        FXGL.onKey(KeyCode.W, () -> movePlayer(0, -1));
        FXGL.onKey(KeyCode.S, () -> movePlayer(0, 1));
        FXGL.onKey(KeyCode.A, () -> movePlayer(-1, 0));
        FXGL.onKey(KeyCode.D, () -> movePlayer(1, 0));
        FXGL.onKeyDown(KeyCode.R, this::restartRun);
        FXGL.onKeyDown(KeyCode.ESCAPE, this::handleEscape);

        FXGL.getGameScene().getRoot().addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMove);
        FXGL.getGameScene().getRoot().addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        FXGL.getGameScene().getRoot().addEventHandler(ScrollEvent.SCROLL, this::handleScroll);
    }

    private void handleMouseMove(MouseEvent event) {
        if (interactionController == null) {
            return;
        }
        if (isInsideMap(event.getX(), event.getY())) {
            int tileX = (int) (event.getX() / TILE_SIZE);
            int tileY = (int) (event.getY() / TILE_SIZE);
            interactionController.onMouseMoveTile(tileX, tileY);
        } else {
            interactionController.onMouseMoveTile(-1, -1);
        }
        refreshInteractionUI();
    }

    private void handleMousePressed(MouseEvent event) {
        if (interactionController == null) {
            return;
        }
        if (stackMenuNode != null) {
            stackMenuNode.hide();
        }
        boolean insideMap = isInsideMap(event.getX(), event.getY());
        if (!insideMap) {
            if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                interactionController.onOutsideClick();
                refreshInteractionUI();
            }
            return;
        }
        int tileX = (int) (event.getX() / TILE_SIZE);
        int tileY = (int) (event.getY() / TILE_SIZE);
        switch (event.getButton()) {
            case PRIMARY -> {
                interactionController.onLeftClickTile(tileX, tileY, this::isInteractiveVisible);
                refreshInteractionUI();
            }
            case SECONDARY -> {
                interactionController.onRightClickTile(tileX, tileY, this::isInteractiveVisible);
                refreshInteractionUI();
            }
            case MIDDLE -> {
                openStackMenu(tileX, tileY);
            }
            default -> {
            }
        }
    }

    private void handleScroll(ScrollEvent event) {
        if (interactionController == null) {
            return;
        }
        if (event.getDeltaY() > 0) {
            interactionController.onScroll(1);
        } else if (event.getDeltaY() < 0) {
            interactionController.onScroll(-1);
        }
        refreshInteractionUI();
    }

    private void openStackMenu(int tileX, int tileY) {
        if (interactiveRegistry == null || stackMenuNode == null) {
            return;
        }
        var stack = interactiveRegistry.getStackAt(tileX, tileY);
        if (stack.isEmpty()) {
            stackMenuNode.hide();
            return;
        }
        if (stack.size() == 1) {
            if (selectionManager != null) {
                selectionManager.select(stack.get(0));
            }
            interactionController.closeContextMenu();
            refreshInteractionUI();
            return;
        }
        stackMenuNode.show(stack, tileX, tileY, TILE_SIZE, FXGL.getAppWidth(), FXGL.getAppHeight(), sidePanelStartX, obj -> {
            if (selectionManager != null) {
                selectionManager.select(obj);
            }
            interactionController.closeContextMenu();
            refreshInteractionUI();
        });
    }

    private boolean isInsideMap(double sceneX, double sceneY) {
        int tileX = (int) (sceneX / TILE_SIZE);
        int tileY = (int) (sceneY / TILE_SIZE);
        return tileX >= 0 && tileY >= 0 && tileX < dungeonWidthTiles && tileY < dungeonHeightTiles;
    }

    private void handleEscape() {
        if (stackMenuNode != null) {
            stackMenuNode.hide();
        }
        if (interactionController != null) {
            if (interactionController.getContextMenuModel() != null) {
                interactionController.closeContextMenu();
                refreshInteractionUI();
                return;
            }
            if (selectionManager != null && selectionManager.getState().selectedId() != null) {
                selectionManager.clear();
                refreshInteractionUI();
                return;
            }
        }
        FXGL.getGameController().exit();
    }

    private void handleActionInvoked(String actionCode) {
        if (interactionController == null) {
            return;
        }
        interactionController.invokeAction(actionCode);
        var selectedId = selectionManager.getState().selectedId();
        if (selectedId != null) {
            var obj = interactiveRegistry.getById(selectedId);
            if (obj != null) {
                if (!"INSPECT".equals(actionCode)) {
                    FXGL.getNotificationService().pushNotification(Messages.format("ui.toast.placeholder", actionCode));
                } else {
                    FXGL.getNotificationService().pushNotification("Inspecting " + obj.getDisplayName());
                }
            }
        }
        refreshInteractionUI();
    }

    private void restartRun() {
        if (!deterministicSeed) {
            seed = System.currentTimeMillis();
        }
        hud.hideGameOver();
        generateAndInit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
