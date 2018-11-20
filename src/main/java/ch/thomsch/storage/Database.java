package ch.thomsch.storage;

import java.util.HashMap;

/**
 * @author Thomsch
 */
public interface Database {
    void persist(HashMap<String, String> ancestry);

    /**
     * Imports raw data into the storage. Replaces previous metrics.
     *
     * @param data the data to import
     */
    void setRaw(ClassStore data);

    /**
     * Imports metric fluctuation data into the storage. Replaces previous metric fluctuations.
     *
     * @param data the data to import.
     */
    void setDiff(ClassStore data);
}
