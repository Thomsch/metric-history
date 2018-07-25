package ch.thomsch;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Thomsch
 */
public class ApplicationTest {

    private Application application;
    private OutputStream out;

    @Before
    public void setUp() {
        application = new Application();
        out = setupPrintOutput();
    }

    @Test
    public void doMain_ShouldPrintHelp_WhenZeroArguments() {
        application.doMain(new String[0]);

        assertFalse(out.toString().isEmpty());
    }

    @Test
    public void processMongoCommand_ShouldThrowException_WhenNoParametersForMongoCommand() {
        try {
            application.doMain(new String[]{"mongo"});
        } catch (IllegalArgumentException e) {
            assertTrue(out.toString().isEmpty());
            return;
        }
        fail();
    }

    @Test
    public void processMongoCommand_ShouldThrowException_WhenNoParameterForRawSubCommand() {
        try {
            application.doMain(new String[]{"mongo", "raw"});
        } catch (IllegalArgumentException e) {
            assertTrue(out.toString().isEmpty());
            return;
        }
        fail();
    }

    @Test
    public void processMongoCommand_ShouldPrintHelp_WhenUnknownSubCommand() {
        application.doMain(new String[]{"mongo", "unsupported sub-command"});

        assertFalse(out.toString().isEmpty());
    }

    private OutputStream setupPrintOutput() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        return out;
    }
}
