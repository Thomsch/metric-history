package ch.thomsch.database;

import java.util.HashMap;

import ch.thomsch.model.Raw;

/**
 * @author Thomsch
 */
public interface Database {
    void persist(HashMap<String, String> ancestry);

    /**
     * Imports raw data into the database. Cleans all previous data.
     *
     * @param data the data to import
     */
    void persistRaw(Raw data);

    /**
     * Imports metric fluctuation data into the database. Cleans all previous data.
     *
     * @param data the data to import.
     */
    void persistDiff(Raw data);
}
