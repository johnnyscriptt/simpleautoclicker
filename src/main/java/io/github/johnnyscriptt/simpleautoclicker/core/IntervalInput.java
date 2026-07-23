package io.github.johnnyscriptt.simpleautoclicker.core;

import io.github.johnnyscriptt.simpleautoclicker.core.config.AutoclickerConfig;

/**
 * Parses the seconds + milliseconds text boxes into one clamped total and
 * splits a total back into display parts, so "1.5" seconds normalizes to
 * 1 s / 500 ms. Pure logic — no Minecraft imports — so it stays unit-testable.
 */
public final class IntervalInput {
    /** Returned by {@link #parseTotalMs} when the boxes hold nothing usable. */
    public static final int INVALID = -1;

    private IntervalInput() {
    }

    /**
     * Combines a decimal seconds box and a whole-milliseconds box into a total,
     * clamped to the config range. Blank boxes count as zero; both blank — or
     * any unparseable text — returns {@link #INVALID}.
     */
    public static int parseTotalMs(String secondsText, String msText) {
        String seconds = secondsText.trim();
        String millis = msText.trim();
        if (seconds.isEmpty() && millis.isEmpty()) {
            return INVALID;
        }
        if (!seconds.matches("\\d*(\\.\\d*)?") || seconds.equals(".") || !millis.matches("\\d*")) {
            return INVALID;
        }
        long total;
        try {
            total = seconds.isEmpty() ? 0 : Math.round(Double.parseDouble(seconds) * 1000.0);
            total += millis.isEmpty() ? 0 : Long.parseLong(millis);
        } catch (NumberFormatException e) {
            return INVALID;
        }
        return (int) Math.clamp(total,
                AutoclickerConfig.MIN_INTERVAL_MS, AutoclickerConfig.MAX_INTERVAL_MS);
    }

    public static int secondsPart(int totalMs) {
        return totalMs / 1000;
    }

    public static int millisPart(int totalMs) {
        return totalMs % 1000;
    }
}
