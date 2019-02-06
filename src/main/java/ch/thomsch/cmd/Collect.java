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
import ch.thomsch.versioncontrol.VCS;
import ch.thomsch.versioncontrol.VcsBuilder;
import ch.thomsch.versioncontrol.VcsNotFound;
import picocli.CommandLine;

/**
 * Execute a code analyzer for multiple versions and their parents. The results are written on disk.
 */
@CommandLine.Command(
        name = "collect",
        description = "Execute a code analyzer for multiple versions and their parents.")
public class Collect extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Collect.class);

    @CommandLine.Parameters(index = "0", description = "Path to the file containing the revision to analyse. DO NOT " +
            "include the parents of the revisions of interest. This will be retrieved automatically.")
    private String revisionFile;

    @CommandLine.Parameters(index = "1", description = "Path to the executable to collect metrics.")
    private String executable;

    @CommandLine.Parameters(index = "2", description = "Path to the folder containing the source code or the project.")
    private String projectPath;

    @CommandLine.Parameters(index = "3", description = "Path to the folder where the results should be extracted.")
    private String outputPath;

    @CommandLine.Parameters(index = "4", description = "Name of the project.")
    private String projectName;

    @CommandLine.Option(names = {"-r", "--repository"}, arity = "0..1", description = "Path to the folder containing .git folder. If omitted, will be searched in the project path.")
    private String repository;

    @Override
    public void run() {
        revisionFile = normalizePath(revisionFile);
        executable = normalizePath(executable);
        projectPath = normalizePath(projectPath);

        if (repository == null) {
            repository = projectPath;
        } else {
            repository = normalizePath(repository);
        }
        outputPath = normalizePath(outputPath);

        final RevisionRepo revisionRepo = new RevisionRepo(new SimpleCommitReader());
        final List<String> revisions = revisionRepo.load(revisionFile);

        try(VCS vcs = VcsBuilder.create(repository)) {
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

            vcs.restoreVersion();
        } catch (VcsNotFound e) {
            logger.error("Failed to access repository {}", repository);
        } catch (Exception e) {
            logger.error("Failed to dispose the repository.");
        }
    }
}
