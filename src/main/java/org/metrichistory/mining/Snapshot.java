package org.metrichistory.mining;

import org.metrichistory.analyzer.sourcemeter.SourceMeterConverter;
import org.metrichistory.model.FormatException;
import org.metrichistory.versioncontrol.Vcs;
import org.metrichistory.versioncontrol.VcsBuilder;
import org.metrichistory.versioncontrol.VcsCleanupException;
import org.metrichistory.versioncontrol.VcsNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Mines the metric fluctuations for one version and convert the results to RAW format.
 */
public class Snapshot {
    private static final Logger logger = LoggerFactory.getLogger(Snapshot.class);

    private final Collector collector;
    private final String repositoryPath;

    public Snapshot(Collector collector, String repositoryPath) {
        this.collector = collector;
        this.repositoryPath = repositoryPath;
    }

    public void execute(String version, String outputFile) throws VcsNotFound, VcsCleanupException,
            IOException, FormatException {
        try (Vcs vcs = VcsBuilder.create(repositoryPath)) {
            collector.analyzeVersion(version);
            vcs.clean();
        }

        final Optional<String> analysisResults = collector.getOutputDirectory(version);
        if(analysisResults.isPresent()) {
            SourceMeterConverter.convert(analysisResults.get(), outputFile);
        } else {
            final String message = "The chosen analyzer is not compatible with the current converter implementation for snapshots";
            System.out.println(message);
            logger.warn(message);
        }
    }
}
