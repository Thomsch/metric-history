package ch.thomsch.cmd;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;
import ch.thomsch.storage.OutputBuilder;
import ch.thomsch.storage.RefactoringDetail;
import ch.thomsch.storage.StoreOutput;
import ch.thomsch.storage.Stores;
import picocli.CommandLine;

/**
 * Filters out metric fluctuations for all given versions of a project that are not desired.
 * The filtered metric fluctuations are stored in a new file.
 */

@CommandLine.Command(
        name = "filter",
        description = "Filters out metric fluctuations for all given versions of a project that are not desired.")
public class Filter extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Filter.class);

    @CommandLine.Parameters(index = "0", description = "Path of the file produced by 'diff' command.")
    private String changesFile;

    @CommandLine.Parameters(index = "1", description = "Path of the file containing each refactoring.")
    private String refactoringsFile;

    @CommandLine.Option(names = {"-a"}, description = "Path of the file where the results will be stored. Prints " +
            "results in the standard output if omitted.")
    private String outputFile;

    @Override
    public void run() {
        refactoringsFile = normalizePath(refactoringsFile);
        changesFile = normalizePath(changesFile);

        try {
            final ClassStore model = Stores.loadClasses(changesFile);
            final HashMap<String, RefactoringDetail> detailedRefactorings = loadRefactorings(refactoringsFile);

            final HashMap<String, List<String>> changeSet = aggregateClassesForEachRevision(detailedRefactorings);
            final ClassStore results = filter(model, changeSet);

            final StoreOutput output = OutputBuilder.create(outputFile);
            output.export(results);
        } catch (Exception e) {
            logger.error("An error occurred:", e);
        }
    }

    private HashMap<String, List<String>> aggregateClassesForEachRevision(
            HashMap<String, RefactoringDetail> detailedRefactorings) {
        final HashMap<String, List<String>> changeSet = new HashMap<>();

        detailedRefactorings.forEach((revision, refactoringDetail) -> {
            changeSet.put(revision, new ArrayList<>(refactoringDetail.getClasses()));
        });

        return changeSet;
    }

    /**
     * Filters the class store to keep only the versions of the classes selected.
     *
     * @param changes   the class store to filter
     * @param revisions the list of classes per revision to keep
     * @return the new instance of class store that has been filtered
     */
    private ClassStore filter(ClassStore changes, HashMap<String, List<String>> revisions) {

        final ClassStore filteredChanges = new ClassStore();

        revisions.forEach((revision, classes) -> {
            final ArrayList<String> missingClasses = new ArrayList<>();

            for (String className : classes) {
                final Metrics revisionMetrics = changes.getMetric(revision, className);

                if (revisionMetrics == null) {
                    missingClasses.add(className);
                }

                filteredChanges.addMetric(revision, className, revisionMetrics);
            }

            if (missingClasses.size() > 0) {
                logger.warn("{} - Ignored {} classes ([{}])", revision, missingClasses.size(), String.join(",",
                        missingClasses));
            }
        });
        return filteredChanges;
    }

    private HashMap<String, RefactoringDetail> loadRefactorings(String refactoringsPath) throws IOException {
        final HashMap<String, RefactoringDetail> detailedRefactorings = new HashMap<>();
        try (CSVParser parser =
                     CSVFormat.RFC4180.withFirstRecordAsHeader().withDelimiter(';').parse(new FileReader(refactoringsPath))) {
            for (CSVRecord record : parser) {
                final String revision = record.get(0);
                final String refactoringType = record.get(1);
                final String description = record.get(2);

                RefactoringDetail detail = detailedRefactorings.get(revision);
                if (detail == null) {
                    detail = new RefactoringDetail();
                    detailedRefactorings.put(revision, detail);
                }

                if (!refactoringType.equalsIgnoreCase("Move Source Folder")
                        && !refactoringType.equalsIgnoreCase("Rename Package")) {
                    detail.addRefactoring(refactoringType, description);
                }
            }
        }
        return detailedRefactorings;
    }
}
