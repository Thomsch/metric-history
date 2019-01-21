package ch.thomsch;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import ch.thomsch.model.Ancestry;

/**
 * @author Thomsch
 */
public class AncestryTest {

    @Test
    public void loadingAncestryAndExportingItShouldProduceSameResults() throws IOException {
        Ancestry ancestry = new Ancestry(null, null);
        File actual = File.createTempFile("ancestry-test", "tmp");
        actual.deleteOnExit();

        final File expected = new File("src/test/resources/ancestry.csv");
        Ancestry.load(expected.getAbsolutePath());

        try (CSVPrinter printer = ancestry.getPrinter(actual.getAbsolutePath())) {
            ancestry.export(printer);
        }

        FileUtils.contentEquals(expected, actual);
    }
}
