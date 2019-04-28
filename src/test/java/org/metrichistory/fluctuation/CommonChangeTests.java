package org.metrichistory.fluctuation;

import org.metrichistory.model.Metrics;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests common behaviours between implementations of {@link Computer}.
 */
final class CommonChangeTests {

    private CommonChangeTests() {
    }

    static void computesShouldMakeDifference(Computer changes) {
        final Metrics a = new Metrics(1.0);
        final Metrics b = new Metrics(1.0);
        final Metrics c = new Metrics(2.0);

        final Metrics adiffb = changes.compute(b, a);
        final Metrics bdiffc = changes.compute(c, b);
        final Metrics cdiffb = changes.compute(b, c);

        assertArrayEquals(new Double[]{0.0}, adiffb.get().toArray());
        assertArrayEquals(new Double[]{1.0}, bdiffc.get().toArray());
        assertArrayEquals(new Double[]{-1.0}, cdiffb.get().toArray());
    }

    static void computesShouldRespectOrder(Computer changes) {
        final Metrics a = new Metrics(1.0, 2.0, 3.0);
        final Metrics b = new Metrics(10.0, 20.0, 30.0);

        final Metrics actual = changes.compute(b, a);

        final Double[] expected = {9.0, 18.0, 27.0};
        assertArrayEquals(expected, actual.get().toArray());
    }

    static void bothMissingOperandsReturnNull(Computer changes) {
        assertNull(changes.compute(null, null));
    }
}
