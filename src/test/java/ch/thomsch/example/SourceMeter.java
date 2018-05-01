package ch.thomsch.example;

import java.io.IOException;

import ch.thomsch.MetricHistory;
import ch.thomsch.export.Reporter;
import ch.thomsch.loader.ModifiedRMinerReader;
import ch.thomsch.loader.RMinerReader;
import ch.thomsch.versioncontrol.GitRepository;

/**
 * @author TSC
 */
public final class SourceMeter {

    private static final String EXECUTABLE =
            "C:\\Users\\Thomas\\Projets\\sgl-project\\tools\\SourceMeter\\Java\\SourceMeterJava.exe";
    private static final String SOURCEMETER_OUTPUT = "C:\\Users\\Thomas\\Projets\\sgl-project\\data\\sourcemeter";

    public static void main(String[] args) throws IOException {
        toyExample();
        dagger();
        dagger2();
        jfreechart();
    }

    private static void jfreechart() throws IOException {
        final String REVISION_FILE = "../data/revisions/jfreechart.csv";
        final String REPOSITORY = "C:\\Users\\Thomas\\Projets\\sgl-project\\mined-repositories\\jfreechart";
        final String RESULTS_FILE = "../data/metrics/jfreechart.csv";

        final MetricHistory metricHistory = new MetricHistory(
                new ch.thomsch.metric.SourceMeter(EXECUTABLE,
                        SOURCEMETER_OUTPUT,
                        "jfreechart",
                        REPOSITORY),
                new Reporter(),
                new ModifiedRMinerReader());

        metricHistory.collect(REVISION_FILE, GitRepository.get(REPOSITORY), RESULTS_FILE);
    }

    private static void dagger2() throws IOException {
        final String REVISION_FILE = "../data/revisions/dagger2.csv";
        final String REPOSITORY = "C:\\Users\\Thomas\\Projets\\sgl-project\\mined-repositories\\dagger2";
        final String RESULTS_FILE = "../data/metrics/dagger2.csv";

        final MetricHistory metricHistory = new MetricHistory(
                new ch.thomsch.metric.SourceMeter(EXECUTABLE,
                        SOURCEMETER_OUTPUT,
                        "dagger2",
                        REPOSITORY),
                new Reporter(),
                new ModifiedRMinerReader());

        metricHistory.collect(REVISION_FILE, GitRepository.get(REPOSITORY), RESULTS_FILE);
    }

    private static void dagger() throws IOException {
        final String REVISION_FILE = "../data/revisions/dagger.csv";
        final String REPOSITORY = "C:\\Users\\Thomas\\Projets\\sgl-project\\mined-repositories\\dagger";
        final String RESULTS_FILE = "../data/metrics/dagger.csv";

        final MetricHistory metricHistory = new MetricHistory(
                new ch.thomsch.metric.SourceMeter(EXECUTABLE,
                        SOURCEMETER_OUTPUT,
                        "dagger",
                        REPOSITORY),
                new Reporter(),
                new ModifiedRMinerReader());

        metricHistory.collect(REVISION_FILE, GitRepository.get(REPOSITORY), RESULTS_FILE);
    }

    private static void toyExample() throws IOException {
        final String REVISION_FILE = "../data/revisions/toy-refactorings.csv";
        final String REPOSITORY = "C:\\Users\\Thomas\\Projets\\sgl-project\\mined-repositories\\refactoring-toy" +
                "-example";
        final String RESULTS_FILE = "../data/metrics/toy-refactorings-metrics.csv";

        final MetricHistory metricHistory = new MetricHistory(
                new ch.thomsch.metric.SourceMeter(EXECUTABLE,
                        SOURCEMETER_OUTPUT,
                        "toy-example",
                        REPOSITORY),
                new Reporter(),
                new RMinerReader());

        metricHistory.collect(REVISION_FILE, GitRepository.get(REPOSITORY), RESULTS_FILE);
    }
}
