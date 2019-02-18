package ch.thomsch;

import org.junit.Before;
import org.junit.Test;

import ch.thomsch.fluctuation.VersionComparator;
import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VersionComparatorTest {

    private ClassStore model;
    private VersionComparator versionComparator;

    @Before
    public void setUp() throws Exception {
        model = setupModel();
        versionComparator = new VersionComparator();
    }

    @Test
    public void calculate() {
        ClassStore result = versionComparator.fluctuations("a", "b", model);

        assertEquals(2, result.getClasses("a").size());
        assertEqualsMetrics(new Metrics(-0.1, 0.5, 0.0), result.getMetric("a", "X"));
        assertEqualsMetrics(new Metrics(0.1,0.0,5.0), result.getMetric("a", "Y"));

        result = versionComparator.fluctuations("d", "e", model);
        assertEquals(1, result.getClasses("d").size());
        assertEqualsMetrics(new Metrics(1.0,-5.0,21.0), result.getMetric("d", "X"));

        result = versionComparator.fluctuations("e", "f", model);
        assertEquals(1, result.getClasses("e").size());
        assertEqualsMetrics(new Metrics(0.0,0.0,0.0), result.getMetric("e", "X"));
    }

    @Test
    public void calculate_ShouldReturnEmpty_WhenVersionHasNoMetrics() {
        final ClassStore result = versionComparator.fluctuations("g", "a", model);

        assertNotNull(result);
        assertEquals(0, result.getVersions().size());
    }

    private ClassStore setupModel() {
        final ClassStore classStore = new ClassStore();
        classStore.addMetric("a", "X", new Metrics(0.0, 1.0, 10.0));
        classStore.addMetric("a", "Y", new Metrics(0.1, 0.5, 10.0));
        classStore.addMetric("a", "W", new Metrics(2.0, 3.0, 4.0));
        classStore.addMetric("b", "X", new Metrics(0.1, 0.5, 10.0));
        classStore.addMetric("b", "Y", new Metrics(0.0, 0.5, 5.0));
        classStore.addMetric("b", "Z", new Metrics(0.0, 0.5, 5.0));
        classStore.addMetric("c", "X", new Metrics(Double.NaN, Double.NaN, Double.NaN));
        classStore.addMetric("d", "X", new Metrics(6.0, 5.0, 1.0));
        classStore.addMetric("e", "X", new Metrics(5.0, 10.0, -20.0));
        classStore.addMetric("f", "X", new Metrics(5.0, 10.0, -20.0));
        return classStore;
    }

    private void assertEqualsMetrics(Metrics expected, Metrics actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }
}
