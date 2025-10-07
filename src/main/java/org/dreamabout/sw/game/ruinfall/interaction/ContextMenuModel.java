package org.dreamabout.sw.game.ruinfall.interaction;

import java.util.List;

public record ContextMenuModel(String objectId, List<InteractiveActionItem> actions) {
}
