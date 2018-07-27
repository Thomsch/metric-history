package ch.thomsch.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an ordered collection of unlabelled metrics.
 */
public class Metrics {
    private final ArrayList<Double> metrics;

    public Metrics(Double... metrics) {
        this.metrics = new ArrayList<>(Arrays.asList(metrics));
    }

    /**
     * Add a metric in the next position.
     *
     * @param metric the metric to add
     */
    public void add(double metric) {
        metrics.add(metric);
    }

    /**
     * Return the number of metrics in this instance.
     */
    public int size() {
        return metrics.size();
    }

    /**
     * Return the metric at the position.
     *
     * @param i the position
     * @return the metric
     * @throws IndexOutOfBoundsException if there is no metric at the position
     */
    public Double get(int i) {
        return metrics.get(i);
    }

    public List<Double> get() {
        return metrics;
    }
}
