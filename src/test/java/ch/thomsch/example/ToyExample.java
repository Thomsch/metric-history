package ch.thomsch.example;

import java.io.IOException;

import ch.thomsch.Collector;
import ch.thomsch.GitRepository;
import ch.thomsch.MetricHistory;
import ch.thomsch.Reporter;

/**
 * @author TSC
 */
public final class ToyExample {

    private static final String REVISION_FILE = "src/test/resources/toy-refactorings.csv";
    private static final String REPOSITORY = "../refactoring-toy-example";
    private static final String RESULTS_FILE = "./toy-refactorings-metrics.csv";

    public static void main(String[] args) throws IOException {
        new MetricHistory(new Collector(), new Reporter())
                .collect(REVISION_FILE, GitRepository.get(REPOSITORY), RESULTS_FILE);
    }
}
