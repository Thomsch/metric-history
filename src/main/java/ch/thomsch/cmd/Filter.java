package ch.thomsch.cmd;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;
import ch.thomsch.storage.OutputBuilder;
import ch.thomsch.storage.RefactoringDetail;
import ch.thomsch.storage.Stores;
import ch.thomsch.storage.TradeoffOutput;

/**
 *
 */
public class Filter extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Filter.class);

    private String changesFile;
    private String refactoringsFile;
    private String outputFile;

    @Override
    public String getName() {
        return "filter";
    }

    @Override
    public boolean parse(String[] parameters) {
        if (parameters.length < 2 || parameters.length > 3) {
            return false;
        }

        refactoringsFile = normalizePath(parameters[0]);
        changesFile = normalizePath(parameters[1]);

        for (String parameter : Arrays.copyOfRange(parameters, 2, parameters.length)) {
            final String[] split = parameter.split("=");

            switch (split[0]) {
                case "-o":
                    outputFile = split[1];
                    break;

                default:
                    logger.warn("Unknown option '{}' with option '{}'", split[0], split[1]);
                    return false;
            }
        }
        return true;
    }

    @Override
    public void execute() throws IOException {
        final ClassStore model = Stores.loadClasses(changesFile);
        final HashMap<String, RefactoringDetail> detailedRefactorings = loadRefactorings(refactoringsFile);

        final HashMap<String, List<String>> changeSet = aggregateClassesForEachRevision(detailedRefactorings);
        final ClassStore results = filter(model, changeSet);

        final TradeoffOutput output = OutputBuilder.create(outputFile);
        output.export(results);
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
     * @param changes the class store to filter
     * @param revisions the list of classes per revision to keep
     * @return the new instance of class store that has been filtered
     */
    private ClassStore filter(ClassStore changes, HashMap<String, List<String>> revisions) {

        final ClassStore filteredChanges = new ClassStore();

        revisions.forEach((revision, classes) -> {
            for (String className : classes) {
                final Metrics revisionMetrics = changes.getMetric(revision, className);

                if(revisionMetrics == null) {
                    logger.warn("No metric found for revision {} in changes", revision);
                    break;
                }

                filteredChanges.addMetric(revision, className, revisionMetrics);
            }
        });
        return filteredChanges;
    }

    private HashMap<String, RefactoringDetail> loadRefactorings(String refactoringsPath) throws IOException {
        final HashMap<String, RefactoringDetail> detailedRefactorings = new HashMap<>();
        try (CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().withDelimiter(';').parse(new FileReader(refactoringsPath))) {
            for (CSVRecord record : parser) {
                final String revision = record.get(0);
                final String refactoringType = record.get(1);
                final String description = record.get(2);

                RefactoringDetail detail = detailedRefactorings.get(revision);
                if (detail == null) {
                    detail = new RefactoringDetail();
                    detailedRefactorings.put(revision, detail);
                }
                detail.addRefactoring(refactoringType, description);
            }
        }
        return detailedRefactorings;
    }

    @Override
    public void printUsage() {
        System.out.println("Usage: metric-history " + getName() + " <refactoring list> <ancestry file> <change file> [-o=<output file>]");
        System.out.println();
        System.out.println("<refactoring list>  is the path of the file containing each refactoring.");
        System.out.println("<changes file>      is the path of the file produced by 'diff' command.");
        System.out.println("<output file>       is the path of the file where the results will be stored.");
    }
}
