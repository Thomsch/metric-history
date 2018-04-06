package ch.thomsch;

/**
 * @author TSC
 */
public class ToyExample {
    public static void main(String[] args) {
        new MetricHistory(new Collector(), new VersionControl(), new Reporter())
                .collect("src/main/resources/toy-refactorings.csv",
                        "../refactoring-toy-example",
                        "./toy-refactorings-metrics.csv");
    }
}
