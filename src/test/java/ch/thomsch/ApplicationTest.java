package ch.thomsch;

import org.junit.Test;

/**
 * @author Thomsch
 */
public class ApplicationTest {

    @Test(expected = IllegalArgumentException.class)
    public void convertDoNotAcceptMoreThanThreeArguments() {
        Application application = new Application();
        application.processConvertCommand(new String[]{"A", "B", "C", "D"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertDoNotAcceptLessThanThreeArguments() {
        Application application = new Application();
        application.processConvertCommand(new String[]{"A", "B"});
    }
}
