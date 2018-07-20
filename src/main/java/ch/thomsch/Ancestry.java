package ch.thomsch;

import ch.thomsch.loader.CommitReader;
import ch.thomsch.versioncontrol.Repository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains the revisions and their respective parents.
 *
 * @author Thomsch
 */
public class Ancestry implements CommitReader {
    private static final Logger logger = LoggerFactory.getLogger(Ancestry.class);

    private final Repository repository;
    private final CommitReader commitReader;

    private final Map<String, String> parents;

    public Ancestry(Repository repository, CommitReader commitReader) {
        this.repository = repository;
        this.commitReader = commitReader;

        parents = new LinkedHashMap<>();
    }

    @Override
    public List<String> make(String revisionFile) {
        final List<String> revisions = commitReader.make(revisionFile);

        for (String revision : revisions) {
            try {
                parents.put(revision, repository.getParent(revision));
            } catch (IOException e) {
                logger.error("I/O error prevented retrieval {}'s parent", revision);
            }
        }
        return revisions;
    }

    /**
     * Export the content of this instance to a CSVPrinter.
     * The order of revisions is the same as the output of the {@link CommitReader}.
     *
     * @param printer the printer
     */
    public void export(CSVPrinter printer) {
        parents.forEach((revision, parent) -> {
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
    static public CSVFormat getFormat() {
        return CSVFormat.RFC4180.withHeader("revision", "parent");
    }

    public CSVPrinter getPrinter(String outputFile) throws IOException {
        return new CSVPrinter(new BufferedWriter(new FileWriter(outputFile)), getFormat());
    }

    public static HashMap<String, String> load(String ancestryFile) {
        HashMap<String, String> ancestry = new LinkedHashMap<>();

        try (CSVParser parser = getParser(ancestryFile)) {
            for (CSVRecord record : parser) {
                ancestry.put(record.get(0), record.get(1));
            }
        }catch (FileNotFoundException e) {
            logger.error("The file " + ancestryFile + " doesn't exists", e);
        } catch (IOException e) {
            logger.error("Error while reading the file " + ancestryFile);
        }

        return ancestry;
    }

    private static CSVParser getParser(String file) throws IOException {
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new FileReader(file));
    }
}
