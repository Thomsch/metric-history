package ch.thomsch;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.thomsch.metric.Collector;
import ch.thomsch.metric.SourceMeter;
import ch.thomsch.versioncontrol.GitRepository;
import ch.thomsch.versioncontrol.Repository;

/**
 * @author TSC
 */
public class MetricHistory {
    private static final Logger logger = LoggerFactory.getLogger(MetricHistory.class);

    private final Collector collector;
    private final Reporter reporter;
    private final CommitReader commitReader;

    private final Map<String, Metric> cache;

    public MetricHistory(Collector collector, Reporter reporter, CommitReader reader) {
        this.collector = collector;
        this.reporter = reporter;
        this.commitReader = reader;

        cache = new HashMap<>();
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

        int i = 0;
        for (String revision : revisions) {
            try {
                logger.info("Processing {} ({})", revision, ++i);

                final Collection<File> beforeFiles = new ArrayList<>();
                final Collection<File> afterFiles = new ArrayList<>();

                repository.getChangedFiles(revision, beforeFiles, afterFiles);

                final String parent = repository.getParent(revision);

                final Metric before = collectCachedMetrics(repository, beforeFiles, parent);
                final Metric current = collectCachedMetrics(repository, afterFiles, revision);

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

        cache.clear();
        final long elapsed = System.nanoTime() - beginning;
        logger.info("Task completed in {}", Duration.ofNanos(elapsed));
    }

    private Metric collectCachedMetrics(Repository repository, Collection<File> files, String revision) throws
            GitAPIException {
        final Metric cachedMetrics = cache.get(revision);
        if (cachedMetrics != null) {
            return cachedMetrics;
        }

        repository.checkout(revision);
        final Metric metrics = collector.collect(repository.getDirectory(), files, revision);
        cache.put(revision, metrics);
        return metrics;
    }

    public static void main(String[] args) {
        String revisionFile = FilenameUtils.normalize(args[0]);
        String executable = FilenameUtils.normalize(args[1]);
        String project = FilenameUtils.normalize(args[2]);
        String executableOutput = FilenameUtils.normalize(args[3]);
        String projectName = args[4];

        try {
            Collector collector = new SourceMeter(executable, executableOutput, projectName, project);
            MetricHistory metricHistory = new MetricHistory(collector, new Reporter(), new ModifiedRMinerReader());
            metricHistory.collect(revisionFile, GitRepository.get(project), "./output.csv");
        } catch (IOException e) {
            logger.error("Resource access problem", e);
        } catch (Exception e) {
            logger.error("Something went wrong", e);
        }
    }
}
