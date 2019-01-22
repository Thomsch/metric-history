package ch.thomsch.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the metrics for a project at different granularity.
 */
public class MetricDump {
    public static final MetricDump EMPTY = new MetricDump();

    private final Map<String, Metrics> map;

    public MetricDump() {
        map = new HashMap<>();
    }

    /**
     * Return the list of classes in their canonical name.
     *
     * @return the list of classes
     */
    public Collection<String> getClasses() {
        return map.keySet();
    }

    /**
     * Get the metrics associated to a class.
     *
     * @param className the canonical name of the class
     * @return the metrics
     */
    public Metrics getMetric(String className) {
        return map.get(className);
    }

    /**
     * Add or replace the metrics for a class.
     *
     * @param className the class
     * @param metric    the metrics
     */
    public void add(String className, Metrics metric) {
        map.put(className, metric);
    }
}
