package ch.thomsch.example;

import ch.thomsch.Collector;
import ch.thomsch.MetricHistory;
import ch.thomsch.Reporter;
import ch.thomsch.GitProvider;

/**
 * @author TSC
 */
public class ToyExample {

    public static final String REVISION_FILE = "src/main/resources/toy-refactorings.csv";
    public static final String REPOSITORY = "../refactoring-toy-example";
    public static final String RESULTS_FILE = "./toy-refactorings-metrics.csv";

    public static void main(String[] args) {
        new MetricHistory(new Collector(), new GitProvider(), new Reporter())
                .collect(REVISION_FILE, REPOSITORY, RESULTS_FILE);
    }
}
