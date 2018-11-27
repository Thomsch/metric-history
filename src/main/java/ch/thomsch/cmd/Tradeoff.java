package ch.thomsch.cmd;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;
import ch.thomsch.storage.RefactoringDetail;
import ch.thomsch.storage.Stores;

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
        final HashMap<String, String> ancestry = ch.thomsch.Ancestry.load(ancestryFile);
        if (ancestry.isEmpty()) return;

        ClassStore model = null;
        try {
            model = Stores.loadClasses(rawFile);
        } catch (IOException e) {
            logger.error("I/O error while reading file {}", rawFile);
        }

        final HashMap<String, RefactoringDetail> detailedRefactorings = new HashMap<>();
        try (CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().withDelimiter(';').parse(new FileReader(refactorings))) {
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
        } catch (FileNotFoundException e) {
            logger.error("The file " + ancestryFile + " doesn't exists", e);
        } catch (IOException e) {
            logger.error("Error while reading the file " + ancestryFile);
        }

        final ClassStore finalModel = model;

        final HashMap<String, Metrics> results = new HashMap<>();
        final ch.thomsch.fluctuation.Difference diff = new ch.thomsch.fluctuation.Difference();

        detailedRefactorings.forEach((revision, refactoringDetail) ->
        {
            final List<Metrics> relevantMetrics = new ArrayList<>();
            for (String className : refactoringDetail.getClasses()) {
                System.out.println(String.format("Parent: %s, Revision: %s", ancestry.get(revision), revision));
                final Metrics revisionMetrics = finalModel.getMetric(revision, className);
                final Metrics parentMetrics = finalModel.getMetric(ancestry.get(revision), className);

                if (revisionMetrics != null && parentMetrics != null) {
                    final Metrics result = diff.computes(parentMetrics, revisionMetrics);
                    relevantMetrics.add(result);
                }
            }

            if (!relevantMetrics.isEmpty()) {
                results.put(revision, sum(relevantMetrics));
            }
        });

        results.forEach((revision, metrics) -> System.out.println(String.format("%s,%s", revision, metrics.toString())));
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
}
