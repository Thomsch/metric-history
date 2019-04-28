package org.metrichistory.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MeasureStoreTest {

    private MeasureStore instance;
    private Metrics dummy;

    @Before
    public void setUp() {
        instance = new MeasureStore();
        dummy = new Metrics();
    }

    @Test
    public void testAddingMetrics() {
        instance.add("alpha", "A", dummy);
        instance.add("alpha", "B", dummy);

        assertEquals(1, instance.versionCount());
        assertEquals(2, instance.artifacts("alpha").size());
        assertSameContent(Arrays.asList("A", "B"), instance.artifacts("alpha"));
    }

    private void assertSameContent(Collection<String> expected, Collection<String> actual) {
        assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
    }

    @Test
    public void cyclesAreSupported() {
        /*
          It would be more work to make them illegal. Version control systems don'time allow them anyway.
         */
        instance.add("alpha", "A", dummy);
        instance.add("beta", "A", dummy);
        instance.add("gamma", "A", dummy);

        assertEquals(3, instance.versionCount());
    }

    @Test
    public void numberOfParentsIsDifferentFromNumberOfRevisions() {
        instance.add("alpha", "A", dummy);
        instance.add("beta", "B", dummy);
        instance.add("beta", "A", dummy);
        instance.add("delta", "B", dummy);
        instance.add("gamma", "A", dummy);

        assertEquals(4, instance.versionCount());

        instance.add("epsilon", "C", dummy);

        assertEquals(5, instance.versionCount());
    }

    @Test
    public void newInstancesShouldHaveZeroData() {
        assertEquals(0, instance.versionCount());
    }

    @Test
    public void nullMetricShouldBeIgnored() {
        instance.add("alpha", "A", null);

        assertEquals(0, instance.versionCount());
    }

    @Test
    public void nullMetricShouldBeIgnoredWhenAddedToExistingRevision() {
        instance.add("alpha", "A", dummy);
        instance.add("alpha", "B", null);

        assertEquals(1, instance.versionCount());
        assertEquals(1, instance.artifacts("alpha").size());
    }

    @Test
    public void nullClassShouldBeIgnored() {
        instance.add("alpha", null, dummy);
        instance.add("alpha", null, null);

        assertEquals(0, instance.versionCount());
    }

    @Test
    public void nullClassShouldBeIgnoredWhenAddedToExistingRevision() {
        instance.add("alpha", "A", dummy);
        instance.add("alpha", null, dummy);
        instance.add("alpha", null, null);

        assertEquals(1, instance.versionCount());
        assertEquals(1, instance.artifacts("alpha").size());
    }

    @Test
    public void hasSingleRevision_ShouldReturnTrue_WhenOnlyOneVersionHasBeenAdded() {
        instance.add("alpha", "A", dummy);

        assertTrue(instance.hasSingleVersion());
    }

    @Test
    public void hasSingleRevision_ShouldReturnTrue_WhenMoreOrLessThanOneVersionHasBeenAdded() {
        assertFalse(instance.hasSingleVersion());

        instance.add("alpha", "A", dummy);
        instance.add("beta", "A", dummy);

        assertFalse(instance.hasSingleVersion());
    }
}
