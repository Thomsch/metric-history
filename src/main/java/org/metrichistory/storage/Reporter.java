package org.metrichistory.storage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Write results in a CSV file.
 */
public class Reporter {
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
        Files.createDirectories(Paths.get(outputFile).getParent());
        fileWriter = new FileWriter(outputFile);
        printer = new CSVPrinter(fileWriter, CSVFormat.EXCEL.withDelimiter(DELIMITER));
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
}
