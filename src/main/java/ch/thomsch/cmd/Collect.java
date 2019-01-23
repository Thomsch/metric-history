package ch.thomsch.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import ch.thomsch.mining.Analyzer;
import ch.thomsch.mining.Collector;
import ch.thomsch.mining.SourceMeter;
import ch.thomsch.model.Genealogy;
import ch.thomsch.storage.RevisionRepo;
import ch.thomsch.storage.loader.SimpleCommitReader;
import ch.thomsch.versioncontrol.GitVCS;
import picocli.CommandLine;

/**
 * Execute a code analyzer for multiple versions and their parents. The results are written on disk.
 */
@CommandLine.Command(
        name = "collect",
        description = "Execute a code analyzer for multiple versions and their parents.")
public class Collect extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Collect.class);

    @CommandLine.Parameters(description = "Path to the file containing the revision to analyse. DO NOT " +
            "include the parents of the revisions of interest. This will be retrieved automatically.")
    private String revisionFile;

    @CommandLine.Parameters(description = "Path to the executable to collect metrics.")
    private String executable;

    @CommandLine.Parameters(description = "Path to the folder containing the source code or the project.")
    private String projectPath;

    @CommandLine.Parameters(description = "Path to the folder containing .git folder. It can also be set to 'same' if" +
            " it's the same as <project path>.")
    private String repository;

    @CommandLine.Parameters(description = "Path to the folder where the results should be extracted.")
    private String outputPath;

    @CommandLine.Parameters(description = "Name of the project.")
    private String projectName;

    @Override
    public void execute() throws Exception {
        final RevisionRepo revisionRepo = new RevisionRepo(new SimpleCommitReader());
        final List<String> revisions = revisionRepo.load(revisionFile);
        final GitVCS vcs = GitVCS.get(repository);
        final Analyzer analyzer = new SourceMeter(executable, outputPath, projectName, projectPath);
        final Collector collector = new Collector(analyzer, vcs);

        final Genealogy genealogy = new Genealogy(vcs);
        genealogy.addRevisions(revisions);

        final List<String> analysisTargets = genealogy.getUniqueRevisions();

        logger.info("Read {} distinct revisions", revisions.size());

        final long beginning = System.nanoTime();
        int i = 0;
        for (String revision : analysisTargets) {
            logger.info("Processing {} ({})", revision, ++i);
            collector.analyzeRevision(revision, projectPath);
        }
        final long elapsed = System.nanoTime() - beginning;
        logger.info("Analysis completed in {}", Duration.ofNanos(elapsed));

        vcs.close();
    }

    @Override
    public void run() {
        revisionFile = normalizePath(revisionFile);
        executable = normalizePath(executable);
        projectPath = normalizePath(projectPath);

        if (repository.equalsIgnoreCase("same")) {
            repository = projectPath;
        } else {
            repository = normalizePath(repository);
        }
        outputPath = normalizePath(outputPath);

        try {
            execute();
        } catch (Exception e) {
            logger.error("An error occurred:", e);
        }
    }
}
