package ch.thomsch.metric;

import java.io.File;
import java.util.Collection;

import ch.thomsch.Metric;

/**
 * @author TSC
 */
public interface Collector {
    /**
     * Computes the metrics for the element in the folder.
     *
     * @param folder the path to the folder.
     * @return the metrics for this project
     */
    Metric collect(String folder);

    /**
     * Computes the metrics for the whole folder and then filter the results for the files.
     * Only java files that do not end with "Test(s)" are considered.
     *
     * @param folder the path to the folder.
     * @param files  the files to which the results are filtered.
     * @param revision the revision currently collected.
     * @return the metrics for this project
     */
    Metric collect(String folder, Collection<File> files, String revision);
}
