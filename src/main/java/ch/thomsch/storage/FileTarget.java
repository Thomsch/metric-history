package ch.thomsch.storage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ch.thomsch.model.MeasureStore;
import ch.thomsch.model.Metrics;

/**
 * Exports measures to a single file.
 */
public class FileTarget extends SaveTarget {

    private static final Logger logger = LoggerFactory.getLogger(FileTarget.class);

    private final File file;
    private boolean append;

    FileTarget(File file) {
        this.file = file;
        append = false;
    }

    @Override
    public void export(MeasureStore measureStore) {
        try(CSVPrinter printer = buildPrinter()) {
            for (String version : measureStore.versions()) {
                exportVersion(version, measureStore, printer);
            }

            if(!append) append = true; // Only overwrite the first time.
        } catch (IOException e) {
            logger.error("Unable to write on file {}", file, e);
        }
    }

    private CSVPrinter buildPrinter() throws IOException {
        final CSVFormat format = Stores.getFormat(!append);
        return new CSVPrinter(new BufferedWriter(new FileWriter(file, append)), format);
    }

    private void exportVersion(String version, MeasureStore measureStore, CSVPrinter printer) throws IOException {
        for (String artifact : measureStore.artifacts(version)) {
            final Metrics metrics = measureStore.get(version, artifact);

            if(metrics == null) {
                logger.warn("There is no metric for class {} at revision {}", artifact, version);
                continue;
            }

            printer.printRecord(format(version, artifact, metrics));
        }
    }

    private Object[] format(String version, String artifact, Metrics metrics) {
        final ArrayList<Object> result = new ArrayList<>();
        result.add(version);
        result.add(artifact);
        result.addAll(metrics.get());

        return result.toArray();
    }
}
