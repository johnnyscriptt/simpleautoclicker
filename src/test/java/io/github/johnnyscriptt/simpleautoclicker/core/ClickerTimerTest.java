package io.github.johnnyscriptt.simpleautoclicker.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClickerTimerTest {

    @Test
    void disabledTimerNeverFires() {
        ClickerTimer timer = new ClickerTimer();
        for (int i = 0; i < 10; i++) {
            assertFalse(timer.tick(1));
        }
    }

    @Test
    void toggleReturnsNewState() {
        ClickerTimer timer = new ClickerTimer();
        assertFalse(timer.isEnabled());
        assertTrue(timer.toggle());
        assertTrue(timer.isEnabled());
        assertFalse(timer.toggle());
        assertFalse(timer.isEnabled());
    }

    @Test
    void firstTickAfterEnableFiresImmediately() {
        ClickerTimer timer = new ClickerTimer();
        timer.toggle();
        assertTrue(timer.tick(4));
    }

    @Test
    void intervalOneFiresEveryTick() {
        ClickerTimer timer = new ClickerTimer();
        timer.toggle();
        for (int i = 0; i < 10; i++) {
            assertTrue(timer.tick(1));
        }
    }

    @Test
    void intervalFourFiresEveryFourthTick() {
        ClickerTimer timer = new ClickerTimer();
        timer.toggle();
        for (int tick = 1; tick <= 12; tick++) {
            boolean fired = timer.tick(4);
            if (tick % 4 == 1) {
                assertTrue(fired, "expected fire on tick " + tick);
            } else {
                assertFalse(fired, "expected no fire on tick " + tick);
            }
        }
    }

    @Test
    void reToggleResetsCooldown() {
        ClickerTimer timer = new ClickerTimer();
        timer.toggle();
        assertTrue(timer.tick(10));
        assertFalse(timer.tick(10));
        timer.toggle();
        timer.toggle();
        assertTrue(timer.tick(10));
    }

    @Test
    void disableStopsFiringAndClearsCooldown() {
        ClickerTimer timer = new ClickerTimer();
        timer.toggle();
        assertTrue(timer.tick(10));
        timer.disable();
        assertFalse(timer.isEnabled());
        assertFalse(timer.tick(10));
        timer.toggle();
        assertTrue(timer.tick(10));
    }

    @Test
    void intervalChangeAppliesAfterPendingFire() {
        ClickerTimer timer = new ClickerTimer();
        timer.toggle();
        assertTrue(timer.tick(4));
        // Shrinking the interval mid-cooldown: the pending cooldown from the
        // 4-tick fire still runs out first.
        assertFalse(timer.tick(2));
        assertFalse(timer.tick(2));
        assertFalse(timer.tick(2));
        assertTrue(timer.tick(2));
        assertFalse(timer.tick(2));
        assertTrue(timer.tick(2));
    }

    @Test
    void nonPositiveIntervalTreatedAsOne() {
        ClickerTimer timer = new ClickerTimer();
        timer.toggle();
        assertTrue(timer.tick(0));
        assertTrue(timer.tick(0));
        assertTrue(timer.tick(-5));
    }
}
