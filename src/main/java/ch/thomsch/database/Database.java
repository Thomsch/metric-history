package ch.thomsch.database;

import java.util.HashMap;

import ch.thomsch.model.ClassStore;

/**
 * @author Thomsch
 */
public interface Database {
    void persist(HashMap<String, String> ancestry);

    /**
     * Imports raw data into the database. Replaces previous metrics.
     *
     * @param data the data to import
     */
    void setRaw(ClassStore data);

    /**
     * Imports metric fluctuation data into the database. Replaces previous metric fluctuations.
     *
     * @param data the data to import.
     */
    void setDiff(ClassStore data);
}
