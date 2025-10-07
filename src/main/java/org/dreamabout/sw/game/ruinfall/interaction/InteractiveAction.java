package org.dreamabout.sw.game.ruinfall.interaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Placeholder actions for this iteration.
 */
public enum InteractiveAction {
    INSPECT(true),
    ATTACK(false),
    LOOT(false),
    TALK(false);

    private final boolean baselineEnabled;

    InteractiveAction(boolean baselineEnabled) {
        this.baselineEnabled = baselineEnabled;
    }

    public boolean isBaselineEnabled() {
        return baselineEnabled;
    }

    public String code() {
        return name();
    }

    public String labelKey() {
        return "actions." + name().toLowerCase();
    }

    /**
     * Build default action items list for a given object type.
     */
    public static List<InteractiveActionItem> defaultItemsFor(InteractiveObjectType type) {
        List<InteractiveActionItem> items = new ArrayList<>();
        for (InteractiveAction a : values()) {
            boolean enabled = a.baselineEnabled;
            switch (a) {
                case ATTACK -> enabled = false; // remains placeholder disabled; would be enabled for ENEMY later
                case LOOT -> enabled = false; // placeholder disabled
                case TALK -> enabled = false; // placeholder disabled
                case INSPECT -> enabled = true; // always enabled
            }
            items.add(new InteractiveActionItem(a.code(), a.labelKey(), enabled));
        }
        return items;
    }
}
