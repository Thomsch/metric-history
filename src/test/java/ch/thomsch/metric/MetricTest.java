package ch.thomsch.metric;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Thomsch
 */
public class MetricTest {

    @Test
    public void constructorShouldPreserveOrder() {
        Double[] expected = new Double[]{1.0, 2.0, 3.0, 4.0, 5.0};
        Metric metric = new Metric(expected);

        assertEquals(5, metric.get().size());
        assertArrayEquals(expected, metric.get().toArray());
    }

    @Test
    public void constructorShouldAddAllArguments() {
        Collection<Double> expected = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        Metric metric = new Metric(1.0, 2.0, 3.0, 4.0, 5.0);

        List<Double> actual = metric.get();

        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void addingAMetric() {
        Metric metric = new Metric();

        metric.add(1.0);

        assertEquals(1, metric.get().size());
        assertEquals(1.0, metric.get().get(0), 0);
    }

    @Test
    public void addShouldPreserveOrder() {
        Metric metric = new Metric(1.0);

        metric.add(2.0);
        assertEquals(2, metric.get().size());
        assertArrayEquals(new Double[]{1.0, 2.0}, metric.get().toArray());

        metric.add(3.0);
        metric.add(4.0);
        assertEquals(4, metric.get().size());
        assertArrayEquals(new Double[]{1.0, 2.0, 3.0, 4.0}, metric.get().toArray());
    }
}
