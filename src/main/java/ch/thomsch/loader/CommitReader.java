package ch.thomsch.loader;

import java.util.List;

/**
 * @author TSC
 */
public interface CommitReader {

    /**
     * Creates a list of the commits containing at least one refactoring.
     *
     * @param filePath The location of the file
     * @return A list containing all the commits, sorted by date
     */
    List<String> load(String filePath);
}
