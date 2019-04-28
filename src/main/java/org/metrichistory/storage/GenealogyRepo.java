package org.metrichistory.storage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.metrichistory.model.Genealogy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.metrichistory.storage.loader.CommitReader;

public class GenealogyRepo {
    private static final Logger logger = LoggerFactory.getLogger(GenealogyRepo.class);

    public HashMap<String, String> load(String ancestryFile) throws IOException {
        final HashMap<String, String> ancestry = new LinkedHashMap<>();

        logger.info("Loading ancestry (" + ancestryFile + ")...");

        try (CSVParser parser = getParser(ancestryFile)) {
            for (CSVRecord record : parser) {
                ancestry.put(record.get(0), record.get(1));
            }
        }
        return ancestry;
    }

    public void export(Genealogy genealogy, String outputFile) {
        try (CSVPrinter writer = getPrinter(outputFile)) {
            export(genealogy, writer);
        } catch (IOException e) {
            logger.error("I/O error with file {}", outputFile, e);
        }
    }

    /**
     * Exports a {@link Genealogy} in a file.
     * The order of revisions is the same as the output of the {@link CommitReader}.
     *
     * @param genealogy the instance to export
     * @param printer the printer
     */
    public void export(Genealogy genealogy, CSVPrinter printer) {
        logger.info("Saving results...");

        genealogy.getMap().forEach((revision, parent) -> {
            try {
                printer.printRecord(revision, parent);
            } catch (IOException e) {
                logger.error("Failed to write {}-{} (revision-parent)", revision, parent);
            }
        });
    }

    /**
     * Returns the format to express the relation of revisions and their parents.
     */
    private static CSVFormat getFormat() {
        return CSVFormat.RFC4180.withHeader("revision", "parent");
    }

    private CSVPrinter getPrinter(String outputFile) throws IOException {
        return new CSVPrinter(new BufferedWriter(new FileWriter(outputFile)), getFormat());
    }

    private static CSVParser getParser(String file) throws IOException {
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new FileReader(file));
    }
}
