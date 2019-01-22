package ch.thomsch.mining;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.thomsch.model.Metrics;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Thomsch
 */
public class MetricsTest {

    @Test
    public void constructorShouldPreserveOrder() {
        Double[] expected = new Double[]{1.0, 2.0, 3.0, 4.0, 5.0};
        Metrics metrics = new Metrics(expected);

        assertEquals(5, metrics.get().size());
        assertArrayEquals(expected, metrics.get().toArray());
    }

    @Test
    public void constructorShouldAddAllArguments() {
        Collection<Double> expected = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        Metrics metrics = new Metrics(1.0, 2.0, 3.0, 4.0, 5.0);

        List<Double> actual = metrics.get();

        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void addingAMetric() {
        Metrics metrics = new Metrics();

        metrics.add(1.0);

        assertEquals(1, metrics.size());
        assertEquals(1.0, metrics.get(0), 0);
    }

    @Test
    public void addShouldPreserveOrder() {
        Metrics metrics = new Metrics(1.0);

        metrics.add(2.0);
        assertEquals(2, metrics.size());
        assertArrayEquals(new Double[]{1.0, 2.0}, metrics.get().toArray());

        metrics.add(3.0);
        metrics.add(4.0);
        assertEquals(4, metrics.size());
        assertArrayEquals(new Double[]{1.0, 2.0, 3.0, 4.0}, metrics.get().toArray());
        assertEquals(1, metrics.get(0), 0);
        assertEquals(2, metrics.get(1), 0);
        assertEquals(3, metrics.get(2), 0);
        assertEquals(4, metrics.get(3), 0);
    }

    @Test
    public void size_ShouldReturnZero_WhenNoMetricIsAdded() {
        Metrics metrics = new Metrics();

        assertEquals(0, metrics.size());
        assertTrue(metrics.get().isEmpty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_ShouldThrowException_WhenEmpty() {
        Metrics metrics = new Metrics();

        metrics.get(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void get_ShouldThrowException_WhenNoMetricAtIndex() {
        Metrics metrics = new Metrics(2.0);

        assertEquals(2.0, metrics.get(0), 0);

        metrics.get(1);
    }

    @Test
    public void hasTradeOff_ShouldReturnFalse_WhenMetricsAreZero() {
        final Metrics metrics = new Metrics(0.0,3.0,0.0);

        assertFalse(metrics.hasTradeOff(0,2));
    }

    @Test
    public void hasTradeOff_ShouldReturnFalse_WhenMetricsArePositive() {
        final Metrics metrics = new Metrics(1.0,3.0,0.0);

        assertFalse(metrics.hasTradeOff(1,0));
    }

    @Test
    public void hasTradeOff_ShouldReturnFalse_WhenOneMetricIsPositive() {
        final Metrics metrics = new Metrics(1.0,3.0,0.0);

        assertFalse(metrics.hasTradeOff(1));
    }

    @Test
    public void hasTradeOff_ShouldReturnFalse_WhenMetricsAreNegative() {
        final Metrics metrics = new Metrics(-1.0,-3.0,-0.0);

        assertFalse(metrics.hasTradeOff(1,0,2));
    }

    @Test
    public void hasTradeOff_ShouldReturnFalse_WhenOneMetricIsNegative() {
        final Metrics metrics = new Metrics(1.0,-3.0,0.0);

        assertFalse(metrics.hasTradeOff(1));
    }

    @Test
    public void hasTradeOff_ShouldReturnTrue_WhenMetricsArePositiveAndNegative() {
        final Metrics metrics = new Metrics(1.0,-3.0,0.0);

        assertTrue(metrics.hasTradeOff(0,1,2));
    }
}
