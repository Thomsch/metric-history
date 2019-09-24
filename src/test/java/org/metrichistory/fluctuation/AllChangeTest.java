package org.metrichistory.fluctuation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metrichistory.model.Metrics;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class AllChangeTest {

    private AllChange changes;

    @BeforeEach
    public void setUp() {
        changes = new AllChange();
    }

    @Test
    public void computesAllowsMissingFirstOperand() {
        final Metrics m = new Metrics(1.0, 2.0, 3.0);

        final Metrics actual = changes.compute(m, null);

        final Double[] expected = {1.0, 2.0, 3.0};
        assertArrayEquals(expected, actual.get().toArray());
    }

    @Test
    public void computesAllowsMissingSecondOperand() {
        final Metrics m = new Metrics(1.0, 2.0, 3.0);

        final Metrics actual = changes.compute(null, m);

        final Double[] expected = {-1.0, -2.0, -3.0};
        assertArrayEquals(expected, actual.get().toArray());
    }

    @Test
    public  void computesShouldMakeDifference() {
        CommonChangeTests.computesShouldMakeDifference(changes);
    }

    @Test
    public  void computesShouldRespectOrder() {
        CommonChangeTests.computesShouldRespectOrder(changes);
    }

    @Test
    public  void bothMissingOperandsReturnNull() {
        CommonChangeTests.bothMissingOperandsReturnNull(changes);
    }
}
