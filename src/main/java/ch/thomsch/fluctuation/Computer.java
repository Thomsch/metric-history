package ch.thomsch.fluctuation;

import ch.thomsch.model.Metrics;

public interface Computer {

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
