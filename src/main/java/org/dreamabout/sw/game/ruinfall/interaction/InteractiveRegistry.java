package org.dreamabout.sw.game.ruinfall.interaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Central registry of interactive objects with tile-based lookup and deterministic priority resolution.
 */
public class InteractiveRegistry {

    private final ConcurrentMap<String, InteractiveObject> byId = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, LinkedHashSet<String>> tileMap = new ConcurrentHashMap<>();

    private static long key(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }

    public void register(InteractiveObject obj) {
        Objects.requireNonNull(obj, "obj");
        if (byId.putIfAbsent(obj.getId(), obj) != null) {
            throw new IllegalArgumentException("Duplicate interactive id: " + obj.getId());
        }
        tileMap.computeIfAbsent(key(obj.getTileX(), obj.getTileY()), k -> new LinkedHashSet<>()).add(obj.getId());
    }

    public void unregister(String id) {
        InteractiveObject removed = byId.remove(id);
        if (removed != null) {
            LinkedHashSet<String> set = tileMap.get(key(removed.getTileX(), removed.getTileY()));
            if (set != null) {
                set.remove(id);
                if (set.isEmpty()) tileMap.remove(key(removed.getTileX(), removed.getTileY()));
            }
        }
    }

    public void moveObject(String id, int newX, int newY) {
        InteractiveObject obj = byId.get(id);
        if (obj == null) return;
        int oldX = obj.getTileX();
        int oldY = obj.getTileY();
        if (oldX == newX && oldY == newY) return;
        LinkedHashSet<String> oldSet = tileMap.get(key(oldX, oldY));
        if (oldSet != null) {
            oldSet.remove(id);
            if (oldSet.isEmpty()) tileMap.remove(key(oldX, oldY));
        }
        obj.setTilePosition(newX, newY);
        tileMap.computeIfAbsent(key(newX, newY), k -> new LinkedHashSet<>()).add(id);
    }

    public List<InteractiveObject> getStackAt(int x, int y) {
        LinkedHashSet<String> set = tileMap.get(key(x, y));
        if (set == null || set.isEmpty()) return List.of();
        // Build ordered list by priority: Enemy > NPC > Chest, preserving insertion order within same type.
        List<InteractiveObject> enemies = new ArrayList<>();
        List<InteractiveObject> npcs = new ArrayList<>();
        List<InteractiveObject> chests = new ArrayList<>();
        for (String id : set) {
            InteractiveObject obj = byId.get(id);
            if (obj == null) continue; // defensive
            switch (obj.getType()) {
                case ENEMY -> enemies.add(obj);
                case NPC -> npcs.add(obj);
                case CHEST -> chests.add(obj);
            }
        }
        List<InteractiveObject> out = new ArrayList<>(enemies.size() + npcs.size() + chests.size());
        out.addAll(enemies);
        out.addAll(npcs);
        out.addAll(chests);
        return Collections.unmodifiableList(out);
    }

    public Optional<InteractiveObject> getPrimaryAt(int x, int y) {
        List<InteractiveObject> stack = getStackAt(x, y);
        if (stack.isEmpty()) return Optional.empty();
        return Optional.of(stack.get(0));
    }

    public InteractiveObject getById(String id) {
        return byId.get(id);
    }
}
