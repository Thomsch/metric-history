package ch.thomsch.metric;

import org.junit.Test;

import ch.thomsch.metric.FileFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Thomsch
 */
public class FileFilterTest {

    private FileFilter filter;


    @Test
    public void acceptShouldFilterElements() {
        filter = new FileFilter();

        assertTrue(filter.accept("a/b/c"));

        filter.addExclusionPattern("b/");

        assertFalse(filter.accept("a/b/c"));
    }

    @Test
    public void productionFilterShouldFilterTests() {
        filter = FileFilter.production();

        assertFalse(filter.accept("src/test/java/A.java"));
        assertFalse(filter.accept("javatests/A.java"));
    }

    @Test
    public void productionFilterShouldFilterExample() {
        filter = FileFilter.production();

        assertFalse(filter.accept("a/b/examples/A.java"));
    }
}
