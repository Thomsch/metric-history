package ch.thomsch.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Thomsch
 */
public class ClassStoreTest {

    private ClassStore instance;
    private Metrics dummy;

    @Before
    public void setUp() {
        instance = new ClassStore();
        dummy = new Metrics();
    }

    @Test
    public void testAddingMetrics() {
        instance.addMetric("alpha", "A", dummy);
        instance.addMetric("alpha", "B", dummy);

        assertEquals(1, instance.getVersions().size());
        assertEquals(2, instance.getClasses("alpha").size());
        assertSameContent(Arrays.asList("A", "B"), instance.getClasses("alpha"));
    }

    private void assertSameContent(Collection<String> expected, Collection<String> actual) {
        assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
    }

    @Test
    public void cyclesAreSupported() {
        /*
          It would be more work to make them illegal. Version control systems don't allow them anyway.
         */
        instance.addMetric("alpha", "A", dummy);
        instance.addMetric("beta", "A", dummy);
        instance.addMetric("gamma", "A", dummy);

        assertEquals(3, instance.getVersions().size());
    }

    @Test
    public void numberOfParentsIsDifferentFromNumberOfRevisions() {
        instance.addMetric("alpha", "A", dummy);
        instance.addMetric("beta", "B", dummy);
        instance.addMetric("beta", "A", dummy);
        instance.addMetric("delta", "B", dummy);
        instance.addMetric("gamma", "A", dummy);

        assertEquals(4, instance.getVersions().size());

        instance.addMetric("epsilon", "C", dummy);

        assertEquals(5, instance.getVersions().size());
    }

    @Test
    public void newInstancesShouldHaveZeroData() {
        assertEquals(0, instance.getVersions().size());
    }
}
