package ch.thomsch.export;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author TSC
 */
public class ReporterTest {

    private File expectedFile;
    private Reporter reporter;

    @Before
    public void setUp() {
        expectedFile = new File("src/test/resources/test.csv");

        if(expectedFile.exists() && !expectedFile.delete()) {
            throw new RuntimeException("Could not set up test file " + expectedFile.getAbsolutePath());
        }

        reporter = new Reporter();
    }

    @After
    public void tearDown() throws Exception {
        reporter.finish();
        final boolean delete = expectedFile.delete();
        if (!delete) {
            throw new RuntimeException("Could not clean up test file " + expectedFile.getAbsolutePath());
        }
    }

    @Test
    public void initializeShouldCreateTestFile() throws IOException {
        reporter.initialize("src/test/resources/test.csv");

        assertTrue(expectedFile.exists());
    }
}
