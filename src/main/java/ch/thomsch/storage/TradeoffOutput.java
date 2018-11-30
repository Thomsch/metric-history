package ch.thomsch.storage;

import java.util.HashMap;

import ch.thomsch.model.Metrics;

/**
 * Exports metrics.
 */
public interface TradeoffOutput {

    /**
     * Exports the metrics of a list of revisions.
     * @param results the pair of revision/metric to export
     * @param whitelist the metrics to include in the export
     */
    void export(HashMap<String, Metrics> results, String ... whitelist);
}
