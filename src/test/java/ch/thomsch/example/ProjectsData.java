package ch.thomsch.example;

import java.io.IOException;

import ch.thomsch.Collector;
import ch.thomsch.MetricHistory;
import ch.thomsch.ModifiedRMinerReader;
import ch.thomsch.Reporter;
import ch.thomsch.versioncontrol.GitRepository;

/**
 * @author TSC
 */
public class ProjectsData {
    private static final String REVISION_FILE = "../mined-repositories/dagger.csv";
    private static final String REPOSITORY = "../mined-repositories/dagger";
    private static final String RESULTS_FILE = "../mined-repositories/results/dagger-metrics.csv";

    public static void main(String[] args) throws IOException {
        final MetricHistory metricHistory = new MetricHistory(
                new Collector(),
                new Reporter(),
                new ModifiedRMinerReader());

        metricHistory.collect(REVISION_FILE, GitRepository.get(REPOSITORY), RESULTS_FILE);
    }
}
