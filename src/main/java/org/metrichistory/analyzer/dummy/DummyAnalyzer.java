package org.metrichistory.analyzer.dummy;

import org.metrichistory.analyzer.Analyzer;
import org.metrichistory.mining.FileFilter;

import java.util.Optional;

/**
 * Dummy analyzer to test metric history without installing a third party analyzer like Sourcemeter.
 */
public class DummyAnalyzer implements Analyzer {
    @Override
    public void analyzeVersion(String version) {

    }

    @Override
    public void postExecute(String version) {

    }

    @Override
    public boolean hasInCache(String version) {
        return true;
    }

    @Override
    public Optional<String> getOutputPath(String version) {
        return Optional.empty();
    }
}
