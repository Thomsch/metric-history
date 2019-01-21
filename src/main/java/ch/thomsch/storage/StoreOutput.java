package ch.thomsch.storage;

import ch.thomsch.model.ClassStore;

/**
 * Exports metrics.
 */
public interface StoreOutput {

    /**
     * Exports the metrics of a list of revisions.
     * @param data the pair of revision/metric to export
     */
    void export(ClassStore data);
}
