package ch.thomsch;

/**
 * @author TSC
 */
public class JUnit4 {

    public static final String REVISION_FILE = "src/main/resources/junit4-refactorings-master.csv";
    public static final String REPOSITORY = "../junit4";
    public static final String RESULTS_FILE = "./junit4-refactorings-metrics.csv";

    public static void main(String[] args) {
        new MetricHistory(new Collector(), new VersionControl(), new Reporter())
                .collect(REVISION_FILE, REPOSITORY, RESULTS_FILE);
    }
}
