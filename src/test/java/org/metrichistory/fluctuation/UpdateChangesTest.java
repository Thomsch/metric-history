package org.metrichistory.fluctuation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metrichistory.model.Metrics;

import static org.junit.jupiter.api.Assertions.assertNull;

public class UpdateChangesTest {

    private UpdateChanges changes;

    @BeforeEach
    public void setUp() {
        changes = new UpdateChanges();
    }

    @Test
    public void computesDoNotAllowsMissingFirstOperand() {
        final Metrics m = new Metrics(1.0, 2.0, 3.0);

        final Metrics actual = changes.compute(m, null);
        assertNull(actual);
    }

    @Test
    public void computesDoNotAllowsMissingSecondOperand() {
        final Metrics m = new Metrics(1.0, 2.0, 3.0);

        final Metrics actual = changes.compute(null, m);
        assertNull(actual);
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
