package org.dreamabout.sw.game.ruinfall.localization;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Centralized access to the interaction resource bundle.
 */
public final class Messages {

    private static final String BUNDLE_NAME = "i18n.messages";
    private static final Messages INSTANCE = new Messages();

    private final ResourceBundle bundle;

    private Messages() {
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
    }

    public static Messages getInstance() {
        return INSTANCE;
    }

    public static String get(String key) {
        return INSTANCE.lookup(key);
    }

    public static String format(String key, Object... args) {
        String pattern = INSTANCE.lookup(key);
        return MessageFormat.format(pattern, args);
    }

    private String lookup(String key) {
        if (key == null || key.isBlank()) {
            return "";
        }
        try {
            return bundle.getString(key);
        } catch (MissingResourceException ex) {
            return key;
        }
    }
}