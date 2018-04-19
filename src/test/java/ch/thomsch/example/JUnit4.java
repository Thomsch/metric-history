package ch.thomsch.example;

import java.io.IOException;

import ch.thomsch.Collector;
import ch.thomsch.MetricHistory;
import ch.thomsch.RMinerReader;
import ch.thomsch.Reporter;
import ch.thomsch.versioncontrol.GitRepository;

/**
 * @author TSC
 */
public final class JUnit4 {

    private static final String REVISION_FILE = "../mined-repositories/junit4.csv";
    private static final String REPOSITORY = "../mined-repositories/junit4";
    private static final String RESULTS_FILE = "../mined-repositories/results/junit4-refactorings-metrics.csv";

    public static void main(String[] args) throws IOException {
        new MetricHistory(new Collector(), new Reporter(), new RMinerReader())
                .collect(REVISION_FILE, GitRepository.get(REPOSITORY), RESULTS_FILE);
    }
}
