package ch.thomsch.metric;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the metrics for a project at different granularity.
 *
 * @author Thomsch
 */
public class MetricDump {
    static final MetricDump EMPTY = new MetricDump();

    private final Map<String, CkMetric> map;

    MetricDump() {
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
    public CkMetric getMetric(String className) {
        return map.get(className);
    }

    /**
     * Add or replace the metrics for a class.
     *
     * @param className the class
     * @param metric    the metrics
     */
    public void add(String className, CkMetric metric) {
        map.put(className, metric);
    }
}
