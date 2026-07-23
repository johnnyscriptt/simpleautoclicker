package io.github.johnnyscriptt.simpleautoclicker.core;

/**
 * Countdown toggle for one mouse button. Enabling resets the cooldown so the
 * first tick after a toggle fires immediately; after a fire the next one waits
 * a full interval. Pure logic — no Minecraft imports — so it stays unit-testable.
 */
public final class ClickerTimer {
    private boolean enabled;
    private int cooldown;

    public boolean isEnabled() {
        return enabled;
    }

    /** Flips the toggle; enabling resets so the next tick fires immediately. Returns the new state. */
    public boolean toggle() {
        enabled = !enabled;
        cooldown = 0;
        return enabled;
    }

    public void disable() {
        enabled = false;
        cooldown = 0;
    }

    /**
     * Called once per client tick with the current interval; reading it live
     * means config edits apply without any resync.
     *
     * @return true when a click should fire this tick
     */
    public boolean tick(int intervalTicks) {
        if (!enabled) {
            return false;
        }
        int interval = Math.max(1, intervalTicks);
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        cooldown = interval - 1;
        return true;
    }
}
