package ch.thomsch.metric;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.thomsch.storage.export.Reporter;
import ch.thomsch.storage.loader.CommitReader;
import ch.thomsch.model.MetricDump;
import ch.thomsch.versioncontrol.Repository;

/**
 * Builds and run {@link Collector}.
 * Example of utilisation:
 * <code>
 * new MetricHistory(new CKMetrics(), new Reporter(), new RefactoringMiner())
 * .collect(REVISION_FILE, GitRepository.get(REPOSITORY), RESULTS_FILE);
 * </code>
 */
public class MetricHistory {
    private static final Logger logger = LoggerFactory.getLogger(MetricHistory.class);

    private final Collector collector;
    private final Reporter reporter;
    private final CommitReader commitReader;

    private final Map<String, MetricDump> cache;
    private final FileFilter filter;

    public MetricHistory(Collector collector, Reporter reporter, CommitReader reader) {
        this.collector = collector;
        this.reporter = reporter;
        this.commitReader = reader;

        cache = new HashMap<>();
        filter = FileFilter.production();
    }

    public void collectRevision(String commitId, Repository repository, String outputFile,
                                String collectorOutputDirectory) {
        final long beginning = System.nanoTime();

        logger.info("Processing single revision {}", commitId);
        logger.info("Output file: {}", outputFile);

        try {
            reporter.initialize(outputFile);
            reporter.printMetaInformation();
        } catch (IOException e) {
            logger.error("Cannot initialize element:", e);
            return;
        }

        try {
            logger.info("Processing {}", commitId);

            final String parent = repository.getParent(commitId);

            final MetricDump current = collectCachedMetrics(repository, commitId);

            SourceMeterConverter.convert(collectorOutputDirectory, outputFile);

            reporter.report(commitId, parent, current);
        } catch (IOException e) {
            logger.error("Cannot write results for revision {}:", commitId, e);
        } catch (GitAPIException e) {
            logger.error("Checkout failure: ", e);
        }


        try {
            reporter.finish();
            repository.close();
        } catch (IOException e) {
            logger.error("Cannot close output file:", e);
        } catch (Exception e) {
            logger.error("Failed to properly close the repository", e);
        }

        cache.clear();
        final long elapsed = System.nanoTime() - beginning;
        logger.info("Task completed in {}", Duration.ofNanos(elapsed));
    }

    /**
     * Collects the metrics before and after for each of the revisions found in the file <code>revisionFile</code>.
     *
     * @param revisionFile Path to the CSV file containing the revisions
     * @param repository   The repository containing the revisions.
     * @param outputFile   Path to the file where the results will be printed
     */
    public void collect(String revisionFile, Repository repository, String outputFile) {
        final long beginning = System.nanoTime();

        final List<String> revisions = commitReader.make(revisionFile);
        logger.info("Read {} distinct revisions", revisions.size());

        try {
            reporter.initialize(outputFile);
            reporter.printMetaInformation();
        } catch (IOException e) {
            logger.error("Cannot initialize element:", e);
            return;
        }

        int i = 0;
        for (String revision : revisions) {
            try {
                logger.info("Processing {} ({})", revision, ++i);

                final String parent = repository.getParent(revision);

                final MetricDump before = collectCachedMetrics(repository, parent);
                final MetricDump current = collectCachedMetrics(repository, revision);

                reporter.report(revision, parent, before, current);
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

        cache.clear();
        final long elapsed = System.nanoTime() - beginning;
        logger.info("Task completed in {}", Duration.ofNanos(elapsed));
    }

    private MetricDump collectCachedMetrics(Repository repository, String revision) throws
            GitAPIException {
        final MetricDump cachedMetrics = cache.get(revision);
        if (cachedMetrics != null) {
            return cachedMetrics;
        }

        repository.checkout(revision);
        final MetricDump metrics = collector.collect(repository.getDirectory(), revision, filter);
        cache.put(revision, metrics);
        collector.afterCollect(revision);
        return metrics;
    }
}
