package ch.thomsch.fluctuation;

import org.junit.Ignore;
import org.junit.Test;

import ch.thomsch.model.Metrics;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

@Ignore
public class ChangeTest {
    protected Computer changes;

    @Test
    public void computesShouldMakeDifference() {
        final Metrics a = new Metrics(1.0);
        final Metrics b = new Metrics(1.0);
        final Metrics c = new Metrics(2.0);

        final Metrics adiffb = changes.compute(a, b);
        final Metrics bdiffc = changes.compute(b, c);
        final Metrics cdiffb = changes.compute(c, b);

        assertArrayEquals(new Double[]{0.0}, adiffb.get().toArray());
        assertArrayEquals(new Double[]{1.0}, bdiffc.get().toArray());
        assertArrayEquals(new Double[]{-1.0}, cdiffb.get().toArray());
    }

    @Test
    public void computesShouldRespectOrder() {
        final Metrics a = new Metrics(1.0, 2.0, 3.0);
        final Metrics b = new Metrics(10.0, 20.0, 30.0);

        final Metrics actual = changes.compute(a, b);

        final Double[] expected = {9.0, 18.0, 27.0};
        assertArrayEquals(expected, actual.get().toArray());
    }

    @Test
    public void bothMissingOperandsReturnNull() {
        assertNull(changes.compute(null, null));
    }
}
