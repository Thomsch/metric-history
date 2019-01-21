package ch.thomsch.storage;

import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;

import ch.thomsch.fluctuation.Differences;
import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;

/**
 * Exports results to a CSV file.
 */
public class CsvOutput implements StoreOutput {
    private static final Logger logger = LoggerFactory.getLogger(CsvOutput.class);

    private final String file;

    CsvOutput(String file) {
        this.file = file;
    }

    @Override
    public void export(ClassStore data) {
        try (CSVPrinter writer = new CSVPrinter(new FileWriter(file), Stores.getFormat())) {
            for (String revision : data.getVersions()) {
                for (String className : data.getClasses(revision)) {
                    final Metrics metric = data.getMetric(revision, className);

                    if(metric == null) {
                        logger.warn("There is no metric for class {} at revision {}", className, revision);
                        break;
                    }
                    Differences.outputMetric(writer, revision, className, metric);
                }
            }
        } catch (IOException e) {
            logger.error("I/O error with file {}", file, e);
        }
    }
}
