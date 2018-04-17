package ch.thomsch;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Write results in a CSV file.
 *
 * @author TSC
 */
public class Reporter {

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
        printer = new CSVPrinter(fileWriter, CSVFormat.EXCEL.withDelimiter(';'));
        printer.printRecord("revision",
                "Lines of code", "Coupling between objects", "Depth inheritance tree",
                "Number of children", "Number of fields", "Number of methods",
                "Response for a class", "Weight method class");
    }

    /**
     * Report a result for a revision.
     *
     * @param result the revision to report
     * @throws IOException when the result cannot be printed
     */
    public void report(DifferentialResult result) throws IOException {
        printer.printRecord(result.format());
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
