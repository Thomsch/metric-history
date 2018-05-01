package ch.thomsch.loader;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import ch.thomsch.CommitReader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author TSC
 */
public class RMinerReaderTest {

    private static final String TEST_FILE = "src/test/resources/test-revisions.csv";

    @Test
    public void reduceList() {
        final RMinerReader reader = new RMinerReader();
        final List<String> expected = Arrays.asList("A", "B", "C");
        final List<String> list = Arrays.asList("A", "A", "B", "B", "C", "C");

        final List<String> result = reader.reduceCommits(list);

        assertEquals(expected, result);
    }

    @Test
    public void loadFile() {
        final CommitReader commitReader = new RMinerReader();
        final List<String> revisions = commitReader.load(TEST_FILE);

        final List<String> expected = Arrays.asList(
                "f7d4f2835ec7dde25356d923ade0eb93f11cf1c9",
                "627b85abfd9c1f6c69b14f9d33fb292868fe6826",
                "347eb809e653d8fb601b9751f7b7971341e1f5ec");

        assertEquals(3, revisions.size());
        assertThat(revisions, is(expected));
    }
}
