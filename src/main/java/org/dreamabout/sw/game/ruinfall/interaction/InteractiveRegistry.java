package org.dreamabout.sw.game.ruinfall.interaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central registry of interactive objects with tile-based lookup and deterministic priority resolution.
 */
public class InteractiveRegistry {

    public interface Listener {
        void onObjectMoved(String id, int oldX, int oldY, int newX, int newY, InteractiveRegistry registry);
        void onObjectUnregistered(String id, int oldX, int oldY, InteractiveRegistry registry);
    }

    private final ConcurrentMap<String, InteractiveObject> byId = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, LinkedHashSet<String>> tileMap = new ConcurrentHashMap<>();
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(Listener l){ if(l!=null) listeners.add(l);}
    public void removeListener(Listener l){ if(l!=null) listeners.remove(l);}

    private static long key(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }
    private static int keyX(long k){ return (int)(k >> 32); }
    private static int keyY(long k){ return (int)k; }

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
            int oldX = removed.getTileX();
            int oldY = removed.getTileY();
            LinkedHashSet<String> set = tileMap.get(key(oldX, oldY));
            if (set != null) {
                set.remove(id);
                if (set.isEmpty()) tileMap.remove(key(oldX, oldY));
            }
            for (Listener l : listeners) {
                l.onObjectUnregistered(id, oldX, oldY, this);
            }
        }
    }

    public void moveObject(String id, int newX, int newY) {
        InteractiveObject obj = byId.get(id);
        if (obj == null) return;
        int currentX = obj.getTileX();
        int currentY = obj.getTileY();
        boolean adapterAlreadyUpdated = (currentX == newX && currentY == newY);

        int oldX = currentX; int oldY = currentY; boolean changed = true;

        if (!adapterAlreadyUpdated) {
            LinkedHashSet<String> oldSet = tileMap.get(key(currentX, currentY));
            if (oldSet != null) {
                oldSet.remove(id);
                if (oldSet.isEmpty()) tileMap.remove(key(currentX, currentY));
            }
            obj.setTilePosition(newX, newY);
        } else {
            long targetKey = key(newX, newY);
            LinkedHashSet<String> targetSet = tileMap.get(targetKey);
            boolean alreadyIndexedAtTarget = targetSet != null && targetSet.contains(id);
            if (!alreadyIndexedAtTarget) {
                // find old location (scan) and remove
                for (Map.Entry<Long, LinkedHashSet<String>> e : tileMap.entrySet()) {
                    if (e.getKey() == targetKey) continue;
                    LinkedHashSet<String> set = e.getValue();
                    if (set.remove(id)) {
                        if (set.isEmpty()) tileMap.remove(e.getKey());
                        oldX = keyX(e.getKey());
                        oldY = keyY(e.getKey());
                        break;
                    }
                }
            } else {
                changed = false; // no move actually happened
            }
        }
        if (!changed) return;
        tileMap.computeIfAbsent(key(newX, newY), k -> new LinkedHashSet<>()).add(id);
        for (Listener l : listeners) {
            l.onObjectMoved(id, oldX, oldY, newX, newY, this);
        }
    }

    public List<InteractiveObject> getStackAt(int x, int y) {
        LinkedHashSet<String> set = tileMap.get(key(x, y));
        if (set == null || set.isEmpty()) return List.of();
        // Build ordered list by priority: Enemy > NPC > Chest, preserving insertion order within same type.
        List<InteractiveObject> enemies = new ArrayList<>();
        List<InteractiveObject> npcs = new ArrayList<>();
        List<InteractiveObject> chests = new ArrayList<>();
        for (String oid : set) {
            InteractiveObject o = byId.get(oid);
            if (o == null) continue; // defensive
            switch (o.getType()) {
                case ENEMY -> enemies.add(o);
                case NPC -> npcs.add(o);
                case CHEST -> chests.add(o);
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
