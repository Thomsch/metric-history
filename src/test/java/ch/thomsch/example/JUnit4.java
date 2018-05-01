package ch.thomsch.example;

import java.io.IOException;

import ch.thomsch.MetricHistory;
import ch.thomsch.RMinerReader;
import ch.thomsch.export.Reporter;
import ch.thomsch.metric.CKMetrics;
import ch.thomsch.versioncontrol.GitRepository;

/**
 * @author TSC
 */
public final class JUnit4 {

    private static final String REVISION_FILE = "../data/revisions/junit4.csv";
    private static final String REPOSITORY = "../mined-repositories/junit4";
    private static final String RESULTS_FILE = "../data/metrics/junit4-refactorings-metrics.csv";

    public static void main(String[] args) throws IOException {
        new MetricHistory(new CKMetrics(), new Reporter(), new RMinerReader())
                .collect(REVISION_FILE, GitRepository.get(REPOSITORY), RESULTS_FILE);
    }
}
