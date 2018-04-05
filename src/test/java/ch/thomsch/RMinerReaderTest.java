package ch.thomsch;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author TSC
 */
public class RMinerReaderTest {

    @Test
    public void reduceList() {
        final RMinerReader reader = new RMinerReader();
        final List<String> expected = Arrays.asList("A", "B", "C");
        final List<String> list = Arrays.asList("A", "A", "B", "B", "C", "C");

        final List<String> result = reader.reduceCommits(list);

        assertEquals(expected, result);
    }
}
