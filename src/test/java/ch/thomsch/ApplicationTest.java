package ch.thomsch;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Thomsch
 */
public class ApplicationTest {

    private Application application;

    @Before
    public void setUp() {
        application = new Application();
    }

    @Test
    public void doMain_ShouldPrintHelp_WhenZeroArguments() {
        application.doMain(new String[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertDoNotAcceptMoreThanThreeArguments() {
        application.processConvertCommand(new String[]{"A", "B", "C", "D"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertDoNotAcceptLessThanThreeArguments() {
        application.processConvertCommand(new String[]{"A", "B"});
    }
}
