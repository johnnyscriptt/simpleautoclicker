package io.github.johnnyscriptt.simpleautoclicker.core;

/** Click and hold state for one mouse button; turning either mode on turns the other off. */
public final class ButtonModes {
    private final ClickerTimer clicker = new ClickerTimer();
    private boolean holding;

    public boolean isClicking() {
        return clicker.isEnabled();
    }

    public boolean isHolding() {
        return holding;
    }

    public boolean isActive() {
        return holding || clicker.isEnabled();
    }

    /** Flips repeated clicking, switching holding off if it turns on. Returns the new state. */
    public boolean toggleClick() {
        boolean on = clicker.toggle();
        if (on) {
            holding = false;
        }
        return on;
    }

    /** Flips holding, switching repeated clicking off if it turns on. Returns the new state. */
    public boolean toggleHold() {
        holding = !holding;
        if (holding) {
            clicker.disable();
        }
        return holding;
    }

    public void disable() {
        clicker.disable();
        holding = false;
    }

    /** True when a click is due this tick; never fires while holding or off. */
    public boolean tickClick(int intervalTicks) {
        return clicker.tick(intervalTicks);
    }
}
