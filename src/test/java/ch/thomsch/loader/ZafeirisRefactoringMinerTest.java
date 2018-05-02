package ch.thomsch.loader;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author TSC
 */
public class ZafeirisRefactoringMinerTest {

    private static final String TEST_FILE = "src/test/resources/zafeiris-refactorings.csv";

    @Test
    public void testLoad() {
        final ZafeirisRefactoringMiner reader = new ZafeirisRefactoringMiner();

        final List<String> revisions = reader.load(TEST_FILE);

        final List<String> expected = Arrays.asList(
                "d4bce13a443cf12da40a77c16c1e591f4f985b47",
                "9a5c33b16d07d62651ea80552e8782974c96bb8a",
                "0bb0526b70870d57cbac9fcc8c4a7346a4ce5879");

        assertEquals(3, revisions.size());
        assertThat(revisions, is(expected));
    }
}
