package ch.thomsch;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ch.thomsch.model.Genealogy;
import ch.thomsch.versioncontrol.VCS;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Thomsch
 */
public class GenealogyTest {

    private Genealogy genealogy;
    private VCS VCS;

    @Before
    public void setUp() throws Exception {
        VCS = mock(VCS.class);
        when(VCS.getParent("a")).thenReturn("b");
        when(VCS.getParent("b")).thenReturn("c");
        when(VCS.getParent("c")).thenReturn("d");
        when(VCS.getParent("d")).thenReturn(null);

        genealogy = new Genealogy(VCS);
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
}
