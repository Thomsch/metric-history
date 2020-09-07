package org.metrichistory.cli;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.metrichistory.model.MeasureStore;
import org.metrichistory.model.Metrics;
import org.metrichistory.storage.OutputBuilder;
import org.metrichistory.storage.RefactoringDetail;
import org.metrichistory.storage.StoreOutput;
import org.metrichistory.storage.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import picocli.CommandLine;

/**
 * Filters metric fluctuations that are associated with a refactorings.
 */
@CommandLine.Command(
        name = "filter-refactoring",
        description = "Filters metric fluctuations that are associated with a refactorings.")
public class FilterRefactoring extends Command {
    private static final Logger logger = LoggerFactory.getLogger(FilterRefactoring.class);

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
            final MeasureStore model = Stores.loadClasses(changesFile);
            final HashMap<String, RefactoringDetail> detailedRefactorings = loadRefactorings(refactoringsFile);

            final HashMap<String, List<String>> changeSet = aggregateClassesForEachRevision(detailedRefactorings);
            final MeasureStore results = filter(model, changeSet);

            final StoreOutput output = OutputBuilder.create(outputFile);
            output.export(results);
        } catch (IOException e) {
            String message = String.format("Metrics fluctuations (%s) cannot be read", changesFile);
            System.err.println(message);
            logger.error(message, e);
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
    private MeasureStore filter(MeasureStore changes, HashMap<String, List<String>> revisions) {

        final MeasureStore filteredChanges = new MeasureStore();

        revisions.forEach((revision, classes) -> {
            final ArrayList<String> missingClasses = new ArrayList<>();

            for (String className : classes) {
                final Metrics revisionMetrics = changes.get(revision, className);

                if (revisionMetrics == null) {
                    missingClasses.add(className);
                }

                filteredChanges.add(revision, className, revisionMetrics);
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
