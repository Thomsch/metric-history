package ch.thomsch;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.thomsch.metric.Collector;
import ch.thomsch.versioncontrol.Repository;

/**
 * @author TSC
 */
public class MetricHistory {
    private static final Logger logger = LoggerFactory.getLogger(MetricHistory.class);

    private final Collector collector;
    private final Reporter reporter;
    private final CommitReader commitReader;

    public MetricHistory(Collector collector, Reporter reporter, CommitReader reader) {
        this.collector = collector;
        this.reporter = reporter;
        this.commitReader = reader;
    }

    /**
     * Collects the metrics before and after for each of the revisions found in the file <code>revisionFile</code>.
     * @param revisionFile Path to the CSV file containing the revisions
     * @param repository The repository containing the revisions.
     * @param outputFile Path to the file where the results will be printed
     */
    public void collect(String revisionFile, Repository repository, String outputFile) {
        final long beginning = System.nanoTime();
        final List<String> revisions = commitReader.load(revisionFile);
        logger.info("Read {} distinct revisions", revisions.size());

        try {
            reporter.initialize(outputFile);
        } catch (IOException e) {
            logger.error("Cannot initialize element:", e);
            return;
        }

        for (String revision : revisions) {
            try {
                logger.info("Processing revision {}", revision);

                final Collection<File> beforeFiles = new ArrayList<>();
                final Collection<File> afterFiles = new ArrayList<>();

                repository.getChangedFiles(revision, beforeFiles, afterFiles);

                final String parent = repository.getParent(revision);

                repository.checkout(parent);
                final Metric before = collector.collect(repository.getDirectory(), beforeFiles, parent);

                repository.checkout(revision);
                final Metric current = collector.collect(repository.getDirectory(), afterFiles, revision);

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
            repository.close();
        } catch (IOException e) {
            logger.error("Cannot close output file:", e);
        } catch (Exception e) {
            logger.error("Failed to properly close the repository", e);
        }
        final long elapsed = System.nanoTime() - beginning;
        logger.info("Task completed in {}", Duration.ofNanos(elapsed));
    }
}
