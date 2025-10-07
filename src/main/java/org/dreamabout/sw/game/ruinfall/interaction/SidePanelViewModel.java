package org.dreamabout.sw.game.ruinfall.interaction;

import java.util.List;

public record SidePanelViewModel(String selectedObjectId, String headerName, String typeLabel, String description, List<InteractiveActionItem> actions) {
}
