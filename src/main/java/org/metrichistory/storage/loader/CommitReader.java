package org.metrichistory.storage.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface CommitReader {

    /**
     * Creates a list of the commits containing at least one refactoring.
     *
     * @param filePath The location of the file
     * @return A list containing all the commits, sorted by date
     * @throws java.io.FileNotFoundException when the file is not found.
     * @throws FileNotFoundException when the file cannot be parsed.
     */
    List<String> make(String filePath) throws FileNotFoundException, IOException;
}
