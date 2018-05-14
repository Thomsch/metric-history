package ch.thomsch.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a collection of metrics unlabelled. One must use their order to identify them.
 *
 * @author Thomsch
 */
public class Metric {

    private final ArrayList<Double> metrics;

    public Metric(Double... metrics) {
        this.metrics = new ArrayList<>(Arrays.asList(metrics));
    }

    public List<Double> get() {
        return metrics;
    }

    public void add(double v) {
        metrics.add(v);
    }
}
