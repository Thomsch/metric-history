package ch.thomsch.database;

import java.util.HashMap;

/**
 * @author Thomsch
 */
public interface Database {
    void persist(HashMap<String, String> ancestry);
}
