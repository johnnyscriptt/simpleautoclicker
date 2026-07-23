package io.github.johnnyscriptt.simpleautoclicker.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntervalInputTest {

    @Test
    void decimalSecondsAloneParse() {
        assertEquals(1500, IntervalInput.parseTotalMs("1.5", ""));
        assertEquals(500, IntervalInput.parseTotalMs(".5", ""));
        assertEquals(2000, IntervalInput.parseTotalMs("2.", ""));
        assertEquals(1234, IntervalInput.parseTotalMs("1.234", ""));
    }

    @Test
    void secondsAndMillisCombine() {
        assertEquals(1500, IntervalInput.parseTotalMs("1", "500"));
        assertEquals(500, IntervalInput.parseTotalMs("", "500"));
        assertEquals(2000, IntervalInput.parseTotalMs("1.5", "500"));
    }

    @Test
    void millisOverflowNormalizesIntoSeconds() {
        assertEquals(3500, IntervalInput.parseTotalMs("1", "2500"));
    }

    @Test
    void resultsAreClampedToConfigRange() {
        assertEquals(50, IntervalInput.parseTotalMs("0", ""));
        assertEquals(50, IntervalInput.parseTotalMs("", "10"));
        assertEquals(10000, IntervalInput.parseTotalMs("15", ""));
        assertEquals(10000, IntervalInput.parseTotalMs("9", "99999"));
    }

    @Test
    void whitespaceIsTolerated() {
        assertEquals(1500, IntervalInput.parseTotalMs(" 1.5 ", " "));
        assertEquals(500, IntervalInput.parseTotalMs("", " 500 "));
    }

    @Test
    void invalidInputReturnsSentinel() {
        assertEquals(IntervalInput.INVALID, IntervalInput.parseTotalMs("", ""));
        assertEquals(IntervalInput.INVALID, IntervalInput.parseTotalMs(".", ""));
        assertEquals(IntervalInput.INVALID, IntervalInput.parseTotalMs("abc", ""));
        assertEquals(IntervalInput.INVALID, IntervalInput.parseTotalMs("1", "x"));
        assertEquals(IntervalInput.INVALID, IntervalInput.parseTotalMs("-1", ""));
        assertEquals(IntervalInput.INVALID, IntervalInput.parseTotalMs("1", "-500"));
        assertEquals(IntervalInput.INVALID, IntervalInput.parseTotalMs("1.5.0", ""));
    }

    @Test
    void partsSplitATotalForDisplay() {
        assertEquals(1, IntervalInput.secondsPart(1500));
        assertEquals(500, IntervalInput.millisPart(1500));
        assertEquals(0, IntervalInput.secondsPart(50));
        assertEquals(50, IntervalInput.millisPart(50));
        assertEquals(10, IntervalInput.secondsPart(10000));
        assertEquals(0, IntervalInput.millisPart(10000));
    }
}
