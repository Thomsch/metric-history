package ch.thomsch.fluctuation;

import org.junit.Before;
import org.junit.Test;

import ch.thomsch.model.Metrics;

import static org.junit.Assert.assertArrayEquals;

public class AllChangeTest extends ChangeTest {

    @Before
    public void setUp() {
        changes = new AllChange();
    }

    @Test
    public void computesAllowsMissingFirstOperand() {
        final Metrics m = new Metrics(1.0, 2.0, 3.0);

        final Metrics actual = changes.compute(null, m);

        final Double[] expected = {1.0, 2.0, 3.0};
        assertArrayEquals(expected, actual.get().toArray());
    }

    @Test
    public void computesAllowsMissingSecondOperand() {
        final Metrics m = new Metrics(1.0, 2.0, 3.0);

        final Metrics actual = changes.compute(m, null);

        final Double[] expected = {-1.0, -2.0, -3.0};
        assertArrayEquals(expected, actual.get().toArray());
    }
}
