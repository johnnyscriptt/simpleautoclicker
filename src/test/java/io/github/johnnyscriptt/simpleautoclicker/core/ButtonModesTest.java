package io.github.johnnyscriptt.simpleautoclicker.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ButtonModesTest {

    @Test
    void startsInactive() {
        ButtonModes modes = new ButtonModes();
        assertFalse(modes.isClicking());
        assertFalse(modes.isHolding());
        assertFalse(modes.isActive());
        assertFalse(modes.tickClick(1));
    }

    @Test
    void toggleHoldReturnsNewState() {
        ButtonModes modes = new ButtonModes();
        assertTrue(modes.toggleHold());
        assertTrue(modes.isHolding());
        assertTrue(modes.isActive());
        assertFalse(modes.toggleHold());
        assertFalse(modes.isHolding());
        assertFalse(modes.isActive());
    }

    @Test
    void toggleClickReturnsNewStateAndFiresImmediately() {
        ButtonModes modes = new ButtonModes();
        assertTrue(modes.toggleClick());
        assertTrue(modes.isClicking());
        assertTrue(modes.isActive());
        assertTrue(modes.tickClick(4));
        assertFalse(modes.toggleClick());
        assertFalse(modes.isActive());
    }

    @Test
    void holdingTurnsClickingOff() {
        ButtonModes modes = new ButtonModes();
        modes.toggleClick();
        modes.toggleHold();
        assertTrue(modes.isHolding());
        assertFalse(modes.isClicking());
        assertFalse(modes.tickClick(1));
    }

    @Test
    void clickingTurnsHoldingOff() {
        ButtonModes modes = new ButtonModes();
        modes.toggleHold();
        modes.toggleClick();
        assertTrue(modes.isClicking());
        assertFalse(modes.isHolding());
    }

    @Test
    void togglingHoldOffLeavesClickingOff() {
        ButtonModes modes = new ButtonModes();
        modes.toggleClick();
        modes.toggleHold();
        modes.toggleHold();
        assertFalse(modes.isActive());
    }

    @Test
    void disableClearsBoth() {
        ButtonModes modes = new ButtonModes();
        modes.toggleHold();
        modes.disable();
        assertFalse(modes.isActive());
        modes.toggleClick();
        modes.disable();
        assertFalse(modes.isActive());
        assertFalse(modes.tickClick(1));
    }
}
