package ch.thomsch;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.List;

/**
 * @author TSC
 */
public class MetricHistory {
    private final Collector collector;
    private final GitProvider versionControl;
    private final Reporter reporter;

    public MetricHistory(Collector collector, GitProvider versionControl, Reporter reporter) {
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
            System.err.println("Cannot initialize element: " + e.getMessage());
            return;
        }

        for (String revision : revisions) {
            try {
                System.out.println("Processing revision " + revision);
                versionControl.checkout(revision);
                final Metric current = collector.collect(repositoryDirectory);
                versionControl.checkoutParent(revision);
                final Metric before = collector.collect(repositoryDirectory);

                reporter.writeResults(revision, current, before);
            } catch (IOException e) {
                System.err.println("Cannot write results for revision " + revision + ": " + e.getMessage());
            } catch (GitAPIException e) {
                System.err.println("Checkout failure: " + e.getMessage());
            }
        }

        try {
            reporter.finish();
            versionControl.checkout("master");
        } catch (IOException e) {
            System.err.println("Cannot close output file: " + e.getMessage());
        } catch (GitAPIException e) {
            System.err.println("Failed to clean version history: " + e.getMessage());
        }
    }
}
