package ch.thomsch;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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

    private Collection<Object> exportMetric(Metric metric) {
        return Arrays.asList(
                metric.getLineOfCode(), metric.getCouplingBetweenObjects(), metric.getDepthInheritanceTree(),
                metric.getNumberOfChildren(), metric.getNumberOfFields(), metric.getNumberOfMethods(),
                metric.getResponseForAClass(), metric.getWeightMethodClass());
    }

    public void writeResults(String revision, Metric current, Metric before) throws IOException {
        printer.printRecord(formatRevision("parent-"+revision, before));
        printer.printRecord(formatRevision(revision, current));
    }

    private Iterable<?> formatRevision(String revision, Metric current) {
        final ArrayList<Object> result = new ArrayList<>();

        result.add(revision);
        result.addAll(exportMetric(current));

        return result;
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
