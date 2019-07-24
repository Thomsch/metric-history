package org.metrichistory.cmd.util;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ProgressIndicatorTest {

    private ByteArrayOutputStream stream;

    @Before
    public void setUp() {
        stream = new ByteArrayOutputStream();
    }

    @Test
    public void update_ShouldPrintCustomPercentage_WhenLessElementsThanMinInterval() {
        final ProgressIndicator indicator = new ProgressIndicator(4, 10, new PrintStream(stream));

        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();

        assertEquals("25.00%\n50.00%\n75.00%\n100.00%\n", stream.toString());
    }

    @Test
    public void update_ShouldPrintPercentage_WhenSameElementsAsInterval() {
        final ProgressIndicator indicator = new ProgressIndicator(10, 10, new PrintStream(stream));

        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();

        assertEquals("10.00%\n20.00%\n30.00%\n40.00%\n50.00%\n60.00%\n70.00%\n80.00%\n90.00%\n100.00%\n", stream.toString());
    }

    @Test
    public void update_ShouldPrintPercentage_WhenMoreElementsThanInterval() {
        final ProgressIndicator indicator = new ProgressIndicator(7, 20, new PrintStream(stream));

        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();
        indicator.update();

        assertEquals("28.57%\n42.86%\n71.43%\n85.71%\n100.00%\n", stream.toString());
    }
}
