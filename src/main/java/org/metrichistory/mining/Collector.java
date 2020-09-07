package org.metrichistory.mining;

import org.metrichistory.analyzer.Analyzer;
import org.metrichistory.versioncontrol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Uses an {@link Analyzer} to analyze a project.
 */
public class Collector {
    private static final Logger logger = LoggerFactory.getLogger(Collector.class);

    private final Analyzer analyzer;

    public Collector(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * Analyze one version if it hasn't been cached.
     * @param version the version to analyze.
     */
    public void analyzeVersion(String version) {
        if(analyzer.hasInCache(version)){
            return;
        }

        analyzer.analyzeVersion(version);
        analyzer.postExecute(version);
    }

    /**
     * Run the {@link Analyzer} in each of the versions contained in <code>versionsToAnalyze</code> using the
     * version control system in <code>repositoryPath</code>.
     * @param versionsToAnalyze The list of versions to analyze.
     * @param repositoryPath The root of the directory under version control.
     * @throws VcsNotFound when <code>repositoryPath</code> cannot be resolved to VCS system.
     * @throws VcsCleanupException when the VCS failed to cleanup resources.
     */
    public void analyzeVersions(List<String> versionsToAnalyze, String repositoryPath) throws VcsNotFound, VcsCleanupException, VcsOperationException {
        final long beginning = System.nanoTime();

        try (Vcs vcs = VcsBuilder.create(repositoryPath)) {
            int i = 0;
            for (String version : versionsToAnalyze) {
                logger.info("Processing {} ({})", version, ++i);
                vcs.clean();
                vcs.checkout(version);
                analyzeVersion(version);
            }

            final long elapsed = System.nanoTime() - beginning;
            logger.info("Analysis completed in {}", Duration.ofNanos(elapsed));

            vcs.restoreVersion();
        }
    }

    /**
     * @see Analyzer#getOutputPath(String)
     */
    public Optional<String> getOutputDirectory(String version) {
        return analyzer.getOutputPath(version);
    }
}
