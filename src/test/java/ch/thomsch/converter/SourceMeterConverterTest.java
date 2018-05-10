package ch.thomsch.converter;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Thomsch
 */
public class SourceMeterConverterTest {

    private SourceMeterConverter converter;

    @Before
    public void setUp() {
        converter = new SourceMeterConverter();
    }

    @Test
    public void convertSingleClass() throws IOException {
        File reference = new File("src/test/resources/conversion/ref.csv");
        File actual = new File("src/test/resources/conversion/classes.csv");
        actual.deleteOnExit();

        CSVPrinter parser = converter.getPrinter(actual.getAbsolutePath());
        converter.convertClassResult(new File("src/test/resources/conversion/source-meter-classes.csv"), "mock",
                parser);
        parser.close();

        assertTrue(FileUtils.contentEquals(reference, actual));
    }

    @Test
    public void convertProject() throws IOException {
        File reference = new File("src/test/resources/conversion/project/ref.csv");
        File actual = new File("src/test/resources/conversion/classes.csv");
        actual.deleteOnExit();

        CSVPrinter parser = converter.getPrinter(actual.getAbsolutePath());
        final String[] revisionFolders = converter.getRevisionFolders("src/test/resources/conversion/project");
        converter.convertProject(revisionFolders, parser);
        parser.close();

        assertTrue(FileUtils.contentEquals(reference, actual));
    }

    @Test
    public void getRevisionFolders() {
        final String[] actual = converter.getRevisionFolders("src/test/resources/conversion/project");

        for (int i = 0; i < actual.length; i++) {
            actual[i] = FilenameUtils.getBaseName(actual[i]);
        }

        assertArrayEquals(new String[]{"abcdef", "ghijk"}, actual);
    }
}
