package ch.thomsch.storage;

import java.util.HashMap;

import ch.thomsch.model.MeasureStore;

public interface Database {
    void persist(HashMap<String, String> ancestry);

    /**
     * Imports raw data into the storage. Replaces previous metrics.
     *
     * @param data the data to import
     */
    void setRaw(MeasureStore data);

    /**
     * Imports metric fluctuation data into the storage. Replaces previous metric fluctuations.
     *
     * @param data the data to import.
     */
    void setDiff(MeasureStore data);
}
