package ch.thomsch.storage;

import ch.thomsch.model.MeasureStore;

/**
 * Exports metrics.
 */
public interface StoreOutput {

    /**
     * Exports the metrics of a list of revisions.
     * @param data the pair of revision/metric to export
     */
    void export(MeasureStore data);
}
