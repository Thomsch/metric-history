package ch.thomsch;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author TSC
 */
public class ModifiedRMinerReaderTest {

    private static final String TEST_FILE = "src/test/resources/test-revisions-modified.csv";

    @Test
    public void loadShouldRemoveExtra() {
        ModifiedRMinerReader reader = new ModifiedRMinerReader();

        final List<String> revisions = reader.load(TEST_FILE);

        final List<String> expected = Arrays.asList(
                "8f4a753b8e1bf12454060f59807302576f9a16d9",
                "ab33f3a83092065469800b472a78fe17b61f4662",
                "728a41cb20df8ebb513a4d0549be6620ea5389a1");

        assertEquals(3, revisions.size());
        assertThat(revisions, is(expected));
    }
}
