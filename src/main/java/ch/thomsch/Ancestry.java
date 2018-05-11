package ch.thomsch;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.thomsch.loader.CommitReader;
import ch.thomsch.versioncontrol.Repository;

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

        parents = new HashMap<>();
    }

    @Override
    public List<String> load(String revisionFile) {
        final List<String> revisions = commitReader.load(revisionFile);

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
    public CSVFormat getFormat() {
        return CSVFormat.RFC4180.withHeader("revision", "parent");
    }
}
