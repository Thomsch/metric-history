package org.metrichistory.fluctuation;

import org.metrichistory.model.Metrics;

/**
 * Compares between two set of metrics.
 */
public interface ChangesComparator {

    /**
     * Computes the difference for each metric between two suite of metric.
     * It uses the order of the metrics given by {@link Metrics#get()}.
     *
     * @param current current
     * @param old previous value
     * @return <code>current</code> - <code>old</code> or null the changes cannot be computed
     * @throws IllegalArgumentException if the metrics are not comparable
     */
    Metrics compute(Metrics current, Metrics old);
}
