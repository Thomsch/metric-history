package ch.thomsch.storage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import ch.thomsch.model.Metrics;

/**
 * Exports results to a CSV file.
 */
public class CsvOutput implements TradeoffOutput {
    private static final Logger logger = LoggerFactory.getLogger(CsvOutput.class);

    private final String file;

    CsvOutput(String file) {
        this.file = file;
    }

    @Override
    public void export(HashMap<String, Metrics> results, String ... whitelist) {
        final int[] indices = Stores.getIndices(whitelist);

        TreeMap<String, Metrics> sortedResults = new TreeMap<>(results);

        try (CSVPrinter writer = new CSVPrinter(new BufferedWriter(new FileWriter(file)), CSVFormat.RFC4180.withHeader("revision", "trade-off"))) {
            sortedResults.forEach((revision, metrics) -> {
                try {
                    writer.printRecord(revision, metrics.hasTradeOff(indices));
                } catch (IOException e) {
                    logger.error("Failed to write result for revision {}", revision);
                }
            });
        } catch (IOException e) {
            logger.error("Cannot write on " + file, e);
        }
    }
}
