package io.github.johnnyscriptt.simpleautoclicker.core.config;

/**
 * User settings, Gson-serialized to config/simpleautoclicker.json by the client
 * layer. Plain mutable fields so Gson needs no adapters; {@link #clamp()} runs
 * after every load and edit so a hand-edited file can't produce nonsense.
 *
 * <p>Intervals are stored in milliseconds; the game only ticks every
 * {@link #MS_PER_TICK} ms, so values are rounded to the nearest tick when used.
 */
public final class AutoclickerConfig {
    public static final int MS_PER_TICK = 50;
    public static final int MIN_INTERVAL_MS = 50;
    public static final int MAX_INTERVAL_MS = 10000;
    public static final int DEFAULT_INTERVAL_MS = 200;

    public int leftIntervalMs = DEFAULT_INTERVAL_MS;
    public int rightIntervalMs = DEFAULT_INTERVAL_MS;

    public void clamp() {
        leftIntervalMs = clampMs(leftIntervalMs);
        rightIntervalMs = clampMs(rightIntervalMs);
    }

    private static int clampMs(int ms) {
        // 0 means "written by a version without the field" — take the default,
        // not the minimum.
        return ms == 0 ? DEFAULT_INTERVAL_MS : Math.clamp(ms, MIN_INTERVAL_MS, MAX_INTERVAL_MS);
    }

    public int leftIntervalTicks() {
        return ticksFromMs(leftIntervalMs);
    }

    public int rightIntervalTicks() {
        return ticksFromMs(rightIntervalMs);
    }

    /** Nearest whole tick for a millisecond interval, never below one tick. */
    public static int ticksFromMs(int ms) {
        return Math.max(1, Math.round(ms / (float) MS_PER_TICK));
    }
}
