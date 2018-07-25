package ch.thomsch.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.thomsch.model.Raw;

/**
 * Represents a collection of metrics unlabelled. One must use their order to identify them.
 *
 * @author Thomsch
 */
public class Metric {
    public static final int NUMBER_OF_SOURCEMETER_METRICS = 52;

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

    /**
     * Transforms the list of metric into a labelled list.
     *
     * @return the map
     */
    public Map<String, Double> convertToSourceMeterFormat() {
        if (metrics.size() != NUMBER_OF_SOURCEMETER_METRICS) {
            throw new IllegalStateException("These metrics are not compatible with the SourceMeter's format");
        }

        final String[] labels = Arrays.copyOfRange(Raw.getHeader(), 2, Raw.getHeader().length);
        HashMap<String, Double> map = new HashMap<>();
        for (int i = 0; i < labels.length; i++) {
            String label = labels[i].toLowerCase();
            map.put(label, metrics.get(i));
        }
        return map;
    }
}
