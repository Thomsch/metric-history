package ch.thomsch.cmd;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import ch.thomsch.Ancestry;
import ch.thomsch.fluctuation.Differences;
import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;
import ch.thomsch.storage.RefactoringDetail;
import ch.thomsch.storage.Stores;

/**
 *
 */
public class Tradeoff extends Command {

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
        if (parameters.length != 3) {
            return false;
        }

        refactorings = normalizePath(parameters[0]);
        ancestryFile = normalizePath(parameters[1]);
        rawFile = normalizePath(parameters[2]);
        return true;
    }

    @Override
    public void execute() throws IOException {
        final HashMap<String, String> ancestry = Ancestry.load(ancestryFile);
        final ClassStore model = Stores.loadClasses(rawFile);
        final HashMap<String, RefactoringDetail> detailedRefactorings = loadRefactorings(refactorings);

        final HashMap<String, Metrics> results = calculateFluctuations(ancestry, model, detailedRefactorings);

        final Output output = new Output();
        results.forEach(output);
    }

    private HashMap<String, Metrics> calculateFluctuations(
            HashMap<String, String> ancestry,
            ClassStore model,
            HashMap<String, RefactoringDetail> detailedRefactorings) {
        final HashMap<String, Metrics> results = new HashMap<>();

        detailedRefactorings.forEach((revision, refactoringDetail) ->
        {
            final List<Metrics> relevantMetrics = new ArrayList<>();
            for (String className : refactoringDetail.getClasses()) {
                final Metrics revisionMetrics = model.getMetric(revision, className);
                final Metrics parentMetrics = model.getMetric(ancestry.get(revision), className);

                final Metrics result = Differences.computes(parentMetrics, revisionMetrics);
                if(result != null)
                    relevantMetrics.add(result);
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
        System.out.println("Usage: metric-history " + getName() + " <refactoring list> <ancestry file> <raw file>");
        System.out.println();
        System.out.println("<refactoring list>  is the path of the file containing each refactoring.");
        System.out.println("<ancestry file>     is the path of the file produced by 'ancestry' command.");
        System.out.println("<raw file>          is the path of the file produced by 'convert' command.");
    }

    private static class Output implements BiConsumer<String, Metrics> {
        int[] indices = Stores.getIndices("LCOM5", "DIT", "CBO", "WMC");
        @Override
        public void accept(String revision, Metrics metrics) {
            System.out.println(String.format("%s,%s", revision, metrics.hasTradeOff(indices)));
        }
    }
}
