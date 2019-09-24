package org.metrichistory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metrichistory.model.Genealogy;
import org.metrichistory.versioncontrol.VCS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenealogyTest {

    private Genealogy genealogy;

    @BeforeEach
    public void setUp() throws Exception {
        final VCS vcs = mock(VCS.class);
        when(vcs.getParent("a")).thenReturn("b");
        when(vcs.getParent("b")).thenReturn("c");
        when(vcs.getParent("c")).thenReturn("d");
        when(vcs.getParent("e")).thenReturn(null);
        when(vcs.getParent("f")).thenReturn("g");
        when(vcs.getParent("h")).thenReturn("i");

        genealogy = new Genealogy(vcs);
    }

    @Test
    public void addRevisions_ShouldIncludeAllRevisions() throws IOException {
        genealogy.addRevisions(Arrays.asList("a", "b", "c"));

        assertEquals(3, genealogy.getMap().size());
        assertEquals(Arrays.asList("a", "b", "c"), new ArrayList<>(genealogy.getMap().keySet()));
        assertEquals("b", genealogy.getMap().get("a"));
        assertEquals("c", genealogy.getMap().get("b"));
        assertEquals("d", genealogy.getMap().get("c"));
    }

    @Test
    public void hasIgnored_ShouldReturnTrue_WhenAParentIsNotFound() {
        genealogy.addRevisions(Arrays.asList("a", "d"));

        assertEquals(1, genealogy.getMap().size());
        assertTrue(genealogy.hasIgnoredRevisions());
        assertEquals(1, genealogy.getIgnoredRevisions().size());
        assertEquals("d", genealogy.getIgnoredRevisions().get(0));
    }

    @Test
    public void getUniqueRevisions_ShouldNotReturnDuplicates() {
        genealogy.addRevisions(Arrays.asList("a", "b", "c", "e", "f", "h"));

        final List<String> result = genealogy.getUniqueRevisions();

        assertEquals(8, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
        assertTrue(result.contains("d"));
        assertTrue(result.contains("f"));
        assertTrue(result.contains("g"));
        assertTrue(result.contains("h"));
        assertTrue(result.contains("i"));
    }
}
