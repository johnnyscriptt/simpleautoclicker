package io.github.johnnyscriptt.simpleautoclicker.core.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoclickerConfigTest {

    @Test
    void defaultsAreTwoHundredMillis() {
        AutoclickerConfig config = new AutoclickerConfig();
        assertEquals(200, config.leftIntervalMs);
        assertEquals(200, config.rightIntervalMs);
    }

    @Test
    void clampRaisesValuesBelowMinimum() {
        AutoclickerConfig config = new AutoclickerConfig();
        config.leftIntervalMs = 10;
        config.rightIntervalMs = -700;
        config.clamp();
        assertEquals(50, config.leftIntervalMs);
        assertEquals(50, config.rightIntervalMs);
    }

    @Test
    void clampTreatsZeroAsAbsentAndTakesDefault() {
        // 0 means "written by a version without the field" — take the default,
        // not the minimum.
        AutoclickerConfig config = new AutoclickerConfig();
        config.leftIntervalMs = 0;
        config.rightIntervalMs = 0;
        config.clamp();
        assertEquals(200, config.leftIntervalMs);
        assertEquals(200, config.rightIntervalMs);
    }

    @Test
    void clampLowersValuesAboveMaximum() {
        AutoclickerConfig config = new AutoclickerConfig();
        config.leftIntervalMs = 10050;
        config.rightIntervalMs = 999999;
        config.clamp();
        assertEquals(10000, config.leftIntervalMs);
        assertEquals(10000, config.rightIntervalMs);
    }

    @Test
    void clampLeavesInRangeValuesAlone() {
        AutoclickerConfig config = new AutoclickerConfig();
        config.leftIntervalMs = 50;
        config.rightIntervalMs = 10000;
        config.clamp();
        assertEquals(50, config.leftIntervalMs);
        assertEquals(10000, config.rightIntervalMs);
    }

    @Test
    void millisConvertToNearestTick() {
        assertEquals(1, AutoclickerConfig.ticksFromMs(50));
        assertEquals(4, AutoclickerConfig.ticksFromMs(200));
        assertEquals(30, AutoclickerConfig.ticksFromMs(1500));
        assertEquals(200, AutoclickerConfig.ticksFromMs(10000));
    }

    @Test
    void oddMillisRoundToNearestTickAndNeverBelowOne() {
        assertEquals(1, AutoclickerConfig.ticksFromMs(74));   // 1.48 ticks
        assertEquals(2, AutoclickerConfig.ticksFromMs(76));   // 1.52 ticks
        assertEquals(1, AutoclickerConfig.ticksFromMs(10));   // rounds to 0, floors to 1
        assertEquals(1, AutoclickerConfig.ticksFromMs(0));
    }

    @Test
    void intervalTickAccessorsUseTheMsFields() {
        AutoclickerConfig config = new AutoclickerConfig();
        config.leftIntervalMs = 1500;
        config.rightIntervalMs = 50;
        assertEquals(30, config.leftIntervalTicks());
        assertEquals(1, config.rightIntervalTicks());
    }
}
