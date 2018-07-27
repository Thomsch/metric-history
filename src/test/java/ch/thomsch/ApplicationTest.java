package ch.thomsch;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    public void processCollectCommand_ShouldPrintHelp_WhenNoParameters() {
        application.doMain(new String[]{"collect"});

        assertFalse(out.toString().isEmpty());
    }

    @Test
    public void processCollectCommand_ShouldThrowException_WhenNotEnoughParameters() {
        try {
            application.doMain(new String[]{"collect", "A", "B"});
        } catch (IllegalArgumentException e) {
            assertTrue(out.toString().isEmpty());
            return;
        }
        fail();
    }

    @Test
    public void processAncestryCommand_ShouldPrintHelp_WhenWhenNoParameters() {
        application.doMain(new String[]{"ancestry"});

        assertFalse(out.toString().isEmpty());
    }

    @Test
    public void processAncestryCommand_ShouldThrowException_WhenNotEnoughParameters() {
        try {
            application.doMain(new String[]{"ancestry", "A", "B"});
        } catch (IllegalArgumentException e) {
            assertTrue(out.toString().isEmpty());
            return;
        }
        fail();
    }

    @Test
    public void processConvertCommand_ShouldPrintHelp_WhenWhenNoParameters() {
        application.doMain(new String[]{"convert"});

        assertFalse(out.toString().isEmpty());
    }

    @Test
    public void processConvertCommand_ShouldThrowException_WhenNotEnoughParameters() {
        try {
            application.doMain(new String[]{"convert", "A"});
        } catch (IllegalArgumentException e) {
            assertTrue(out.toString().isEmpty());
            return;
        }
        fail();
    }

    @Test
    public void processDiffCommand_ShouldPrintHelp_WhenWhenNoParameters() throws IOException {
        application.doMain(new String[]{"diff"});

        assertFalse(out.toString().isEmpty());
    }

    @Test
    public void processDiffCommand_ShouldThrowException_WhenNotEnoughParameters() {
        try {
            application.doMain(new String[]{"diff", "A", "B"});
        } catch (IllegalArgumentException e) {
            assertTrue(out.toString().isEmpty());
            return;
        }
        fail();
    }

    @Test
    public void processMongoCommand_ShouldPrintHelp_WhenNoParameters() throws IOException {
        application.doMain(new String[]{"mongo"});

        assertFalse(out.toString().isEmpty());
    }

    @Test
    public void processMongoCommand_ShouldPrintHelp_WhenNoRawParameters() {
        application.doMain(new String[]{"mongo", "raw"});

        assertFalse(out.toString().isEmpty());
    }

    @Test
    public void processMongoCommand_ShouldThrowException_WhenNotEnoughRawParameters() {
        try {
            application.doMain(new String[]{"mongo", "raw", "A"});
        } catch (IllegalArgumentException e) {
            assertTrue(out.toString().isEmpty());
            return;
        }
        fail();
    }

    @Test
    public void processMongoCommand_ShouldPrintHelp_WhenNoDiffParameters() {
        application.doMain(new String[]{"mongo", "diff"});

        assertFalse(out.toString().isEmpty());
    }

    @Test
    public void processMongoCommand_ShouldThrowException_WhenNotEnoughDiffParameters() {
        try {
            application.doMain(new String[]{"mongo", "diff", "A"});
        } catch (IllegalArgumentException e) {
            assertTrue(out.toString().isEmpty());
            return;
        }
        fail();
    }

    @Test
    public void processMongoCommand_ShouldPrintHelp_WhenNoAncestryParameters() {
        application.doMain(new String[]{"mongo", "ancestry"});

        assertFalse(out.toString().isEmpty());
    }

    @Test
    public void processMongoCommand_ShouldThrowException_WhenNoParameterForAncestrySubCommand() {
        try {
            application.doMain(new String[]{"mongo", "ancestry", "A"});
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
