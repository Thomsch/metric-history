package org.metrichistory.fluctuation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbsoluteChangeTest {

    private AbsoluteChange instance;

    @BeforeEach
    void setUp() {
        instance = new AbsoluteChange();
    }

    @Test
    void noChange() {
        assertEquals(0.0, instance.apply(0.0, 0.0));
        assertEquals(0.0, instance.apply(-5.0, -5.0));
        assertEquals(0.0, instance.apply(5.0, 5.0));
        assertEquals(0.0, instance.apply(100.0, 100.0));
    }

    @Test
    void increase() {
        assertEquals(5.0, instance.apply(10.0, 15.0));
        assertEquals(10.0, instance.apply(-5.0, 5.0));
        assertEquals(1.0, instance.apply(0.0, 1.0));
        assertEquals(1.0, instance.apply(1.0, 2.0));
    }

    @Test
    void decrease() {
        assertEquals(-5.0, instance.apply(15.0, 10.0));
        assertEquals(-10.0, instance.apply(5.0, -5.0));
        assertEquals(-1.0, instance.apply(1.0, 0.0));
        assertEquals(-1.0, instance.apply(2.0, 1.0));
    }
}
