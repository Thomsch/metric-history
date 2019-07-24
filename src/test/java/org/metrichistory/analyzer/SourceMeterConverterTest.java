package org.metrichistory.analyzer;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.metrichistory.analyzer.SourceMeterConverter;
import org.metrichistory.model.FormatException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        CSVPrinter parser = converter.getPrinter(new File(actual.getAbsolutePath()));
        converter.convertClassResult(new File("src/test/resources/conversion/source-meter-classes.csv"), "mock",
                parser);
        parser.close();

        assertTrue(FileUtils.contentEquals(reference, actual));
    }

    @Test
    public void convertProject() throws Exception {
        File reference = new File("src/test/resources/conversion/project/ref.csv");
        File actual = new File("src/test/resources/conversion/classes.csv");
        actual.deleteOnExit();

        CSVPrinter parser = converter.getPrinter(new File(actual.getAbsolutePath()));
        final String[] revisionFolders = converter.getRevisionFolders("src/test/resources/conversion/project");
        Arrays.sort(revisionFolders);
        converter.convertProject(revisionFolders, parser);
        parser.close();

        assertTrue(FileUtils.contentEquals(reference, actual));
    }

    @Test
    public void getRevisionFolders() throws Exception {
        final String[] actual = converter.getRevisionFolders("src/test/resources/conversion/project");

        for (int i = 0; i < actual.length; i++) {
            actual[i] = FilenameUtils.getBaseName(actual[i]);
        }

        Set<String> expected = new HashSet<>(Arrays.asList(new String[]{"abcdef", "ghijk"}));
        assertEquals(expected, new HashSet<>(Arrays.asList(actual)));
    }

    @Test(expected = FormatException.class)
    public void strictDirectoryStructure() throws Exception {
        converter.getRevisionFolders("src/test/resources/conversion/project/java");
    }
}
