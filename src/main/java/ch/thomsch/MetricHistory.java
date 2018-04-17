package ch.thomsch;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author TSC
 */
public class MetricHistory {
    private static final Logger logger = LoggerFactory.getLogger(MetricHistory.class);

    private final Collector collector;
    private final VersionControl versionControl;
    private final Reporter reporter;

    public MetricHistory(Collector collector, VersionControl versionControl, Reporter reporter) {
        this.collector = collector;
        this.versionControl = versionControl;
        this.reporter = reporter;
    }

    /**
     * Collects the metrics before and after for each of the revisions found in the file <code>revisionFile</code>.
     * @param revisionFile Path to the CSV file containing the revisions
     * @param repositoryDirectory Location of revisions' repository
     * @param outputFile Path to the file where the results will be printed
     */
    public void collect(String revisionFile, String repositoryDirectory, String outputFile) {
        final CommitReader commitReader = new RMinerReader();
        final List<String> revisions = commitReader.load(revisionFile);

        try {
            reporter.initialize(outputFile);
            versionControl.initializeRepository(repositoryDirectory);
        } catch (IOException e) {
            logger.error("Cannot initialize element:", e);
            return;
        }

        for (String revision : revisions) {
            try {
                logger.info("Processing revision {}", revision);

                versionControl.checkout(revision);
                final Metric current = collector.collect(repositoryDirectory);
                versionControl.checkoutParent(revision);
                final Metric before = collector.collect(repositoryDirectory);

                final DifferentialResult result = DifferentialResult.build(revision, before, current);
                reporter.report(result);
            } catch (IOException e) {
                logger.error("Cannot write results for revision {}:", revision, e);
            } catch (GitAPIException e) {
                logger.error("Checkout failure: ", e);
            }
        }

        try {
            reporter.finish();
            versionControl.checkout("master");
        } catch (IOException e) {
            logger.error("Cannot close output file:", e);
        } catch (GitAPIException e) {
            logger.error("Failed to clean version history:", e);
        }
    }
}
