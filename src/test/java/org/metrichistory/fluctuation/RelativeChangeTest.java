package org.metrichistory.fluctuation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RelativeChangeTest {
    private RelativeChange instance;

    @BeforeEach
    void setUp() {
        instance = new RelativeChange();
    }

    @Test
    void negativeArgumentsAreNotAllowed() {
        assertThrows(IllegalArgumentException.class, () -> instance.apply(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> instance.apply(Double.NEGATIVE_INFINITY, 5.0));
        assertThrows(IllegalArgumentException.class, () -> instance.apply(5.0, Double.NEGATIVE_INFINITY));
    }

    @Test
    void noChange() {
        assertEquals(0.0, instance.apply(0.0, 0.0));
        assertEquals(0.0, instance.apply(5.0, 5.0));
        assertEquals(0.0, instance.apply(100.0, 100.0));
    }

    @Test
    void increase() {
        assertEquals(0.5, instance.apply(10.0, 15.0));
        assertEquals(5.0, instance.apply(0.0, 5.0));
        assertEquals(5.0, instance.apply(1.0, 6.0));
    }

    @Test
    void decrease() {
        assertEquals(-1.0/3.0, instance.apply(15.0, 10.0));
        assertEquals(-1.0, instance.apply(5.0, 0.0));
        assertEquals(-5.0/6.0, instance.apply(6.0, 1.0));
    }
}
