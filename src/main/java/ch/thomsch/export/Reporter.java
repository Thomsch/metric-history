package ch.thomsch.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.thomsch.metric.MetricDump;

/**
 * Write results in a CSV file.
 *
 * @author TSC
 */
public class Reporter {
    private static final Logger logger = LoggerFactory.getLogger(Reporter.class);

    public static final char DELIMITER = ';';

    private FileWriter fileWriter;
    private CSVPrinter printer;

    /**
     * Opens and write the header.
     *
     * @param outputFile Where to write
     * @throws IOException When the file cannot be opened
     */
    public void initialize(String outputFile) throws IOException {
        fileWriter = new FileWriter(outputFile);
        printer = new CSVPrinter(fileWriter, CSVFormat.EXCEL.withDelimiter(DELIMITER));
    }

    public void printMetaInformation() throws IOException {
        printer.printRecord(DefaultFormatter.getMetaData());
    }

    /**
     * Report a result for a revision.
     *
     * @param line the data for one line
     * @throws IOException when the result cannot be printed
     */
    public void report(Object[] line) throws IOException {
        printer.printRecord(line);
    }

    /**
     * Closes the file
     */
    public void finish() throws IOException {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }

    public void report(String revision, String parent, MetricDump before, MetricDump current) {
        final List<Object[]> lines = DefaultFormatter.format(revision, parent, current);
        lines.addAll(DefaultFormatter.format(parent, null, before));

        for (Object[] line : lines) {
            try {
                report(line);
            } catch (IOException e) {
                logger.error("Couldn't write results for line {}", Arrays.toString(line));
            }
        }
    }
}
