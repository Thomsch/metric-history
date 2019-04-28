package org.metrichistory.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an ordered collection of unlabelled metrics.
 */
public class Metrics {
    private final ArrayList<Double> metrics;

    private final static Double ZERO = 0.0;

    public Metrics(Double... metrics) {
        this.metrics = new ArrayList<>(Arrays.asList(metrics));
    }

    /**
     * Initialize <code>size</code> metrics to zero.
     * @param size the number of metrics
     */
    public Metrics(int size) {
        this.metrics = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            metrics.add(ZERO);
        }
    }

    /**
     * Add a metric in the next position.
     *
     * @param metric the metric to add
     */
    public void add(double metric) {
        if(metric == ZERO) {
            metrics.add(ZERO);
        } else {
            metrics.add(metric);
        }
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

    @Override
    public String toString() {
        return Arrays.toString(metrics.toArray());
    }

    public boolean hasTradeOff(int ... indices) {
        int positive = 0;
        int negative = 0;

        for (int index : indices) {
            if(this.metrics.get(index) > 0) {
                positive++;
            } else if (this.metrics.get(index) < 0){
                negative++;
            }
        }
        return positive > 0 && negative > 0;
    }

    public Metrics copy() {
        final Metrics copy = new Metrics();
        for (Double metric : metrics) {
            copy.add(metric);
        }
        return copy;
    }
}
