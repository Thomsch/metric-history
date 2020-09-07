package org.metrichistory.analyzer;

import org.metrichistory.mining.Collector;
import org.metrichistory.mining.FileFilter;

import java.util.Optional;

/**
 * Compatibility interface for a third party static code analyzer.
 * To add compatibility to an unsupported analyzer, implement this interface.
 */
public interface Analyzer {

    /**
     * Run an analysis for the given version.
     * @param version the version identifier to analyze (e.g., commit hash, svn revision).
     */
    void analyzeVersion(String version);

    /**
     * Is invoked automatically by {@link Collector} after {@link #execute(String, String, FileFilter)}.
     *
     * @param version the version that has been analyzed.
     */
    void postExecute(String version);

    /**
     * Determines if the analyzer has been executed for a given version.
     * @param version the identification of the version.
     * @return <code>true</code> if the analyzer has the results for the version. <code>false</code> otherwise.
     */
    boolean hasInCache(String version);

    /**
     * Returns the folder in which results are stored at the end of the analysis for the given version.
     * @param version the version.
     * @return the absolute path to the folder or empty if the results are not written on the disk.
     */
    Optional<String> getOutputPath(String version);
}
