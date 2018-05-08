package ch.thomsch.export;

import org.junit.Before;
import org.junit.Test;

import ch.thomsch.metric.Metric;

import static org.junit.Assert.assertEquals;

/**
 * @author Thomsch
 */
public class DifferentialResultTest {

    private DifferentialResult result;

    @Before
    public void setUp() {
        final Metric before = new Metric(1, 2, 3, 4, 5, 6, 7, 8);
        final Metric after = new Metric(10, 20, 30, 40, 50, 60, 70, 80);

        result = DifferentialResult.build("test", before, after);
    }

    @Test
    public void formatShouldHaveTheCorrectOrderAndGiveDifferentialResults() {
        final Object[] expected = {"test", 72.0, 9.0, 18.0, 27.0, 36.0, 45.0, 54.0, 63.0};

        int i = 0;
        for (Object actual : result.format()) {
            assertEquals(expected[i], actual);
            i++;
        }
    }
}
