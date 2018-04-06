package ch.thomsch;

/**
 * @author TSC
 */
public class Metric {
    private final double averageLinesOfCodePerClass;
    private final double averageNumberOfMethodsPerClass;

    public Metric(double averageLinesOfCodePerClass, double averageNumberOfMethodsPerClass) {
        this.averageLinesOfCodePerClass = averageLinesOfCodePerClass;
        this.averageNumberOfMethodsPerClass = averageNumberOfMethodsPerClass;
    }

    public double getAverageLinesOfCodePerClass() {
        return averageLinesOfCodePerClass;
    }

    public double getAverageNumberOfMethodsPerClass() {
        return averageNumberOfMethodsPerClass;
    }
}
