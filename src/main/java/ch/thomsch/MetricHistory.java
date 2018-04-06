package ch.thomsch;

import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.List;

/**
 * @author TSC
 */
public class MetricHistory {
    private final Collector collector;
    private final VersionControl versionControl;
    private final Reporter reporter;

    public MetricHistory(Collector collector, VersionControl versionControl, Reporter reporter) {
        this.collector = collector;
        this.versionControl = versionControl;
        this.reporter = reporter;
    }

    /**
     * Collects the metrics for each of the refactorings found in the file <code>refactoringRevisions</code> that has
     * been extracted from the project repository <code>repositoryDirectory</code>.
     * @param refactoringRevisions Path to the CSV file containing the refactoring revisions
     * @param repositoryDirectory Path to the version controlled project
     * @param outputFile Path to the file where the results will be printed
     */
    public void collect(String refactoringRevisions, String repositoryDirectory, String outputFile) {
        final CommitReader commitReader = new RMinerReader();
        final List<String> refactorings = commitReader.load(refactoringRevisions);

        try {
            reporter.initialize(outputFile);
            versionControl.initializeRepository(repositoryDirectory);
        } catch (IOException e) {
            System.err.println("Cannot initialize element: " + e.getMessage());
            return;
        }

        for (String refactoring : refactorings) {
            try {
                System.out.println("Treating refactoring " + refactoring);
                versionControl.checkout(refactoring);
                final Metric current = collector.collect(repositoryDirectory);
                versionControl.checkoutParent(refactoring);
                final Metric before = collector.collect(repositoryDirectory);

                reporter.writeResults(refactoring, current, before);
            } catch (IOException e) {
                System.err.println("Cannot write results for revision " + refactoring + ": " + e.getMessage());
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
