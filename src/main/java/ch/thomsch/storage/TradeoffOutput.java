package ch.thomsch.storage;

import java.util.HashMap;

import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;

/**
 * Exports metrics.
 */
public interface TradeoffOutput {

    /**
     * Exports the metrics of a list of revisions.
     * @param data the pair of revision/metric to export
     */
    void export(ClassStore data);
}
