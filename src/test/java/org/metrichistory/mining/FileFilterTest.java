package org.metrichistory.mining;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
