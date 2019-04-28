package org.metrichistory.storage.loader;

import java.util.List;

public interface CommitReader {

    /**
     * Creates a list of the commits containing at least one refactoring.
     *
     * @param filePath The location of the file
     * @return A list containing all the commits, sorted by date
     */
    List<String> make(String filePath);
}
