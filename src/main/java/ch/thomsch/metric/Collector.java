package ch.thomsch.metric;

/**
 * @author TSC
 */
public interface Collector {
    /**
     * Computes the metrics for the element in the folder.
     * Only java files that do not end with "Test(s)" are considered.
     *
     * @param folder the path to the folder.
     * @param revision the revision currently collected.
     * @return the metrics for this project
     */
    MetricDump collect(String folder, String revision);
}
