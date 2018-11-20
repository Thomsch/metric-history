package ch.thomsch.metric;

/**
 * @author Thomsch
 */
public interface Collector {
    /**
     * Computes the metrics for the element in the folder according to the filter.
     *
     * @param folder   the path to the folder.
     * @param revision the revision currently collected
     * @param filter a non-null filter to apply to this folder.
     *
     * @return the metrics for this project
     */
    MetricDump collect(String folder, String revision, FileFilter filter);

    /**
     * Is invoked automatically by {@link ch.thomsch.MetricHistory} after {@link #collect(String, String, FileFilter)}.
     *
     * @param revision the revision that has been collected.
     */
    void afterCollect(String revision);
}
