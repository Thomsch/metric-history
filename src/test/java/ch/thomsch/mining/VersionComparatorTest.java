package ch.thomsch.mining;

import org.junit.Before;
import org.junit.Test;

import ch.thomsch.model.MeasureStore;
import ch.thomsch.model.Metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VersionComparatorTest {

    private MeasureStore model;
    private VersionComparator versionComparator;

    @Before
    public void setUp() throws Exception {
        model = setupModel();
        versionComparator = new VersionComparator();
    }

    @Test
    public void calculate() {
        MeasureStore result = versionComparator.fluctuations("a", "b", model);

        assertEquals(2, result.artifacts("a").size());
        assertEqualsMetrics(new Metrics(-0.1, 0.5, 0.0), result.get("a", "X"));
        assertEqualsMetrics(new Metrics(0.1,0.0,5.0), result.get("a", "Y"));

        result = versionComparator.fluctuations("d", "e", model);
        assertEquals(1, result.artifacts("d").size());
        assertEqualsMetrics(new Metrics(1.0,-5.0,21.0), result.get("d", "X"));

        result = versionComparator.fluctuations("e", "f", model);
        assertEquals(1, result.artifacts("e").size());
        assertEqualsMetrics(new Metrics(0.0,0.0,0.0), result.get("e", "X"));
    }

    @Test
    public void calculate_ShouldReturnEmpty_WhenVersionHasNoMetrics() {
        final MeasureStore result = versionComparator.fluctuations("g", "a", model);

        assertNotNull(result);
        assertEquals(0, result.versions().size());
    }

    private MeasureStore setupModel() {
        final MeasureStore measureStore = new MeasureStore();
        measureStore.add("a", "X", new Metrics(0.0, 1.0, 10.0));
        measureStore.add("a", "Y", new Metrics(0.1, 0.5, 10.0));
        measureStore.add("a", "W", new Metrics(2.0, 3.0, 4.0));
        measureStore.add("b", "X", new Metrics(0.1, 0.5, 10.0));
        measureStore.add("b", "Y", new Metrics(0.0, 0.5, 5.0));
        measureStore.add("b", "Z", new Metrics(0.0, 0.5, 5.0));
        measureStore.add("c", "X", new Metrics(Double.NaN, Double.NaN, Double.NaN));
        measureStore.add("d", "X", new Metrics(6.0, 5.0, 1.0));
        measureStore.add("e", "X", new Metrics(5.0, 10.0, -20.0));
        measureStore.add("f", "X", new Metrics(5.0, 10.0, -20.0));
        return measureStore;
    }

    private void assertEqualsMetrics(Metrics expected, Metrics actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }
}
