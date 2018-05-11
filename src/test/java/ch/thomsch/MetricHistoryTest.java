package ch.thomsch;

import org.junit.Test;

/**
 * @author Thomsch
 */
public class MetricHistoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void convertDoNotAcceptMoreThanThreeArguments() {
        MetricHistory.processConvertCommand(new String[]{"A", "B", "C", "D"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertDoNotAcceptLessThanThreeArguments() {
        MetricHistory.processConvertCommand(new String[]{"A", "B"});
    }
}
