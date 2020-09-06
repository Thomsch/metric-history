package org.metrichistory.cli.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProjectNameResolverTest {

    @Test
    public void resolve_ShouldReturnTheLastFolderName_OnUnix() {
        final ProjectNameResolver resolver = new ProjectNameResolver("/this/is/a/name");
        assertEquals("name", resolver.get());
    }

    @Test
    public void resolve_ShouldReturnTheLastFolderName_OnWindows() {
        final ProjectNameResolver resolver = new ProjectNameResolver("D:\\this\\is\\a\\name");

        assertEquals("name", resolver.get());
    }

    @Test
    public void resolve_ShouldThrowAnException_WhenParameterIsNull() {
        final ProjectNameResolver resolver = new ProjectNameResolver(null);
        assertThrows(IllegalArgumentException.class, resolver::get);
    }


    @Test
    public void resolve_ShouldThrowAnException_WhenParameterIsEmpty() {
        final ProjectNameResolver resolver = new ProjectNameResolver("");
        assertThrows(IllegalArgumentException.class, resolver::get);
    }
}
