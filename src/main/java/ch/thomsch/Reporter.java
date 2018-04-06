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
 * @author TSC
 */
public class Reporter {

    private FileWriter fileWriter;
    private CSVPrinter printer;

    public void initialize(String outputFile) throws IOException {
        fileWriter = new FileWriter(outputFile);
        printer = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
        printer.printRecord("revision", "loc", "nom", "b-loc", "b-nom");
    }

    public void writeResults(String revision, Metric current, Metric before) throws IOException {
        final Object[] record = prepareRecord(revision, current, before);
        printer.printRecord(record);
    }

    private Object[] prepareRecord(String revision, Metric current, Metric before) {
        final ArrayList<Object> result = new ArrayList<>();

        result.add(revision);
        result.addAll(exportMetric(current));
        result.addAll(exportMetric(before));

        return result.toArray();
    }

    private Collection<Object> exportMetric(Metric metric) {
        return Arrays.asList(metric.getAverageLinesOfCodePerClass(), metric.getAverageNumberOfMethodsPerClass());
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
