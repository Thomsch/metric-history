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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import ch.thomsch.Ancestry;
import ch.thomsch.fluctuation.Differences;
import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;
import ch.thomsch.storage.OutputBuilder;
import ch.thomsch.storage.RefactoringDetail;
import ch.thomsch.storage.Stores;
import ch.thomsch.storage.TradeoffOutput;

/**
 *
 */
public class Tradeoff extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Tradeoff.class);

    private String ancestryFile;
    private String rawFile;
    private String refactorings;
    private String outputFile;
    private String mode;

    @Override
    public String getName() {
        return "tradeoffs";
    }

    @Override
    public boolean parse(String[] parameters) {
        if (parameters.length < 3 || parameters.length > 5) {
            return false;
        }

        refactorings = normalizePath(parameters[0]);
        ancestryFile = normalizePath(parameters[1]);
        rawFile = normalizePath(parameters[2]);

        for (String parameter : Arrays.copyOfRange(parameters, 3, parameters.length)) {
            final String[] split = parameter.split("=");

            switch (split[0]) {
                case "-o":
                    outputFile = split[1];
                    break;

                case "-m":
                    mode = split[1];
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
        final HashMap<String, String> ancestry = Ancestry.load(ancestryFile);
        final ClassStore model = Stores.loadClasses(rawFile);
        final HashMap<String, RefactoringDetail> detailedRefactorings = loadRefactorings(refactorings);

        final HashMap<String, List<String>> changeSet = filterChanges(detailedRefactorings, model);

        final HashMap<String, Metrics> results = calculateFluctuations(ancestry, model, changeSet);

        final TradeoffOutput output = OutputBuilder.create(outputFile);
        output.export(results, "LCOM5", "DIT", "CBO", "WMC");
    }

    private HashMap<String, List<String>> filterChanges(
            HashMap<String, RefactoringDetail> detailedRefactorings, ClassStore model) {
        final HashMap<String, List<String>> changeSet = new HashMap<>();
        if(this.mode == null || this.mode.isEmpty()) {
            detailedRefactorings.forEach((revision, refactoringDetail) -> {
                changeSet.put(revision, new ArrayList<>(refactoringDetail.getClasses()));
            });
        } else if (this.mode.equalsIgnoreCase("all")){
            detailedRefactorings.forEach((revision, ignored) -> {
                final Collection<String> classes = model.getClasses(revision);
                if (classes == null) {
                    System.out.println("no data for revision " + revision);
                } else {
                    changeSet.put(revision, new ArrayList<>(classes));
                }
            });
        } else {
            throw new RuntimeException("Unknown mode '" + this.mode + '\'');
        }
        return changeSet;
    }

    private HashMap<String, Metrics> calculateFluctuations(
            HashMap<String, String> ancestry,
            ClassStore model,
            HashMap<String, List<String>> revisions) {
        final HashMap<String, Metrics> results = new HashMap<>();

        revisions.forEach((revision, classes) -> {
            final List<Metrics> relevantMetrics = new ArrayList<>();
            for (String className : classes) {

                if(!className.endsWith("Test") && !className.endsWith("Tests")) {
                    final Metrics revisionMetrics = model.getMetric(revision, className);
                    final Metrics parentMetrics = model.getMetric(ancestry.get(revision), className);

                    final Metrics result = Differences.computes(parentMetrics, revisionMetrics);

                    if (result != null)
                        relevantMetrics.add(result);
                }
            }

            if (!relevantMetrics.isEmpty()) {
                results.put(revision, sum(relevantMetrics));
            }
        });
        return results;
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

    private Metrics sum(List<Metrics> refactoringChange) {
        final int numMetrics = refactoringChange.get(0).size();
        final Double[] sum = new Double[numMetrics];
        for (int i = 0; i < sum.length; i++) {
            sum[i] = (double) 0;
        }

        for (Metrics metrics : refactoringChange) {
            for (int i = 0; i < metrics.size(); i++) {
                sum[i] += metrics.get(i);
            }
        }
        return new Metrics(sum);
    }

    @Override
    public void printUsage() {
        System.out.println("Usage: metric-history " + getName() + " <refactoring list> <ancestry file> <raw file> [-o=<output file>] [-m=<mode>]");
        System.out.println();
        System.out.println("<refactoring list>  is the path of the file containing each refactoring.");
        System.out.println("<ancestry file>     is the path of the file produced by 'ancestry' command.");
        System.out.println("<raw file>          is the path of the file produced by 'convert' command.");
        System.out.println("<output file>       is the path of the file where the results will be stored.");
        System.out.println("<mode>              'all' uses all the changes in a revision to define a trade-off." +
                "When the parameter is omitted, we only count classes linked to a refactoring.");
    }

}
