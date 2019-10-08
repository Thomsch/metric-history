package org.metrichistory.analyzer;

import org.metrichistory.mining.Collector;
import org.metrichistory.mining.FileFilter;

import java.util.Optional;

/**
 * Abstraction for a third party static code analyzer.
 */
public interface Analyzer {

    /**
     * Execute the analyzer for a given version of a project.
     * @param revision the version of the project to analyze
     * @param folder   the location of the project
     * @param filter a non-null filter to apply to this folder.
     */
    void execute(String revision, String folder, FileFilter filter);

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
