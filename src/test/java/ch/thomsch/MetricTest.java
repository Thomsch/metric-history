package ch.thomsch;

import org.junit.Test;

import ch.thomsch.metric.Metric;

import static org.junit.Assert.assertEquals;

/**
 * @author TSC
 */
public class MetricTest {

    private static final int NUM_METRICS = 8;

    @Test
    public void addShouldAddTheSameMetrics() {
        final Metric a = new Metric(1, 2, 3, 4, 5, 6, 7, 8);
        final Metric b = new Metric(10, 20, 30, 40, 50, 60, 70, 80);

        a.add(b);

        assertEqualsForAll(a, 11, 22, 33, 44, 55, 66, 77, 88);
    }

    private void assertEqualsForAll(Metric metric, int... expected) {
        if (expected.length != NUM_METRICS) {
            throw new IllegalArgumentException("Expected " + NUM_METRICS + ", got " + expected.length);
        }

        assertEquals(expected[0], metric.getCouplingBetweenObjects(), 0);
        assertEquals(expected[1], metric.getDepthInheritanceTree(), 0);
        assertEquals(expected[2], metric.getNumberOfChildren(), 0);
        assertEquals(expected[3], metric.getNumberOfFields(), 0);
        assertEquals(expected[4], metric.getNumberOfMethods(), 0);
        assertEquals(expected[5], metric.getResponseForAClass(), 0);
        assertEquals(expected[6], metric.getWeightMethodClass(), 0);
        assertEquals(expected[7], metric.getLineOfCode(), 0);
    }

    @Test
    public void emptyConstructorShouldBeInitializedToZeros() {
        final Metric metric = new Metric();

        assertEqualsForAll(metric, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}
