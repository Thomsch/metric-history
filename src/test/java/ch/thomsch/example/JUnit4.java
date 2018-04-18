package ch.thomsch.example;

import java.io.IOException;

import ch.thomsch.Collector;
import ch.thomsch.MetricHistory;
import ch.thomsch.Reporter;
import ch.thomsch.versioncontrol.GitRepository;

/**
 * @author TSC
 */
public final class JUnit4 {

    private static final String REVISION_FILE = "src/test/resources/junit4-refactorings-master.csv";
    private static final String REPOSITORY = "../junit4";
    private static final String RESULTS_FILE = "./junit4-refactorings-metrics.csv";

    public static void main(String[] args) throws IOException {
        new MetricHistory(new Collector(), new Reporter())
                .collect(REVISION_FILE, GitRepository.get(REPOSITORY), RESULTS_FILE);
    }
}
