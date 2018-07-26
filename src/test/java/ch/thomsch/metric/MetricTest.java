package ch.thomsch.metric;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        assertEquals(1, metrics.get().size());
        assertEquals(1.0, metrics.get().get(0), 0);
    }

    @Test
    public void addShouldPreserveOrder() {
        Metrics metrics = new Metrics(1.0);

        metrics.add(2.0);
        assertEquals(2, metrics.get().size());
        assertArrayEquals(new Double[]{1.0, 2.0}, metrics.get().toArray());

        metrics.add(3.0);
        metrics.add(4.0);
        assertEquals(4, metrics.get().size());
        assertArrayEquals(new Double[]{1.0, 2.0, 3.0, 4.0}, metrics.get().toArray());
    }

    @Test(expected = IllegalStateException.class)
    public void convertToSourceMeterFormat_ShouldThrowIllegalStateException_WhenTheMetricsAreFromOtherProvider() {
        Metrics metrics = new Metrics(1.0, 2.0, 3.0);

        metrics.convertToSourceMeterFormat();
    }

    @Test
    public void convertToSourceMeterFormat_ShouldReturnLabelledMetrics() {
        Metrics metrics = new Metrics(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0,
                16.0, 17.0, 18.0,
                19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0, 33.0, 34.0, 35.0,
                36.0, 37.0, 38.0,
                39.0, 40.0, 41.0, 42.0, 43.0, 44.0, 45.0, 46.0, 47.0, 48.0, 49.0, 50.0, 51.0, 52.0);
        final List<String> expectedKeys = Arrays.asList("lcom5", "nl", "nle", "wmc", "cbo", "cboi", "nii", "noi", "rfc",
                "ad", "cd", "cloc", "dloc", "pda", "pua", "tcd", "tcloc", "dit", "noa", "noc", "nod", "nop", "lloc",
                "loc", "na", "ng", "nla", "nlg", "nlm", "nlpa", "nlpm", "nls", "nm", "nos", "npa", "npm", "ns", "tlloc",
                "tloc", "tna", "tng", "tnla", "tnlg", "tnlm", "tnlpa", "tnlpm", "tnls", "tnm", "tnos", "tnpa", "tnpm",
                "tns");

        final Map<String, Double> labelledMetrics = metrics.convertToSourceMeterFormat();

        assertContainsKeys(expectedKeys, new HashSet<>(labelledMetrics.keySet()));
        for (int i = 0; i < expectedKeys.size(); i++) {
            final String key = expectedKeys.get(i);
            final Double actual = labelledMetrics.get(key);
            assertEquals(i + 1.0, actual, 0.0);
        }
    }

    private void assertContainsKeys(List<String> expectedKeys, Set<String> labelledMetrics) {
        expectedKeys.forEach(key -> {
            assertTrue(labelledMetrics.contains(key));
            labelledMetrics.remove(key);
        });

        assertTrue(labelledMetrics.isEmpty());
    }

}
