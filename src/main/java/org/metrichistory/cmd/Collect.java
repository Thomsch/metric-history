package org.metrichistory.cmd;

import org.metrichistory.cmd.util.ProjectName;
import org.metrichistory.mining.Analyzer;
import org.metrichistory.mining.Collector;
import org.metrichistory.mining.SourceMeter;
import org.metrichistory.model.Genealogy;
import org.metrichistory.storage.RevisionRepo;
import org.metrichistory.storage.loader.SimpleCommitReader;
import org.metrichistory.versioncontrol.VCS;
import org.metrichistory.versioncontrol.VcsBuilder;
import org.metrichistory.versioncontrol.VcsNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import picocli.CommandLine;

/**
 * Execute a code analyzer for multiple versions and their parents. The results are written on disk.
 */
@CommandLine.Command(
        name = "collect",
        description = "Execute a code analyzer for multiple versions and their parents.")
public class Collect extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Collect.class);

    @CommandLine.Parameters(index = "0", description = "Path to the file containing the versions to analyse.")
    private String versionFile;

    @CommandLine.Parameters(index = "1", description = "Path to the project's folder.")
    private String repositoryPath;

    @CommandLine.Parameters(index = "2", description = "Path of the folder that will contain the results.")
    private String outputPath;

    @CommandLine.Parameters(index = "3", description = "Path to the third-party analyzer.")
    private String executable;

    @CommandLine.Option(names = {"-p", "--include-parents"}, arity = "0..1", description = "Specifies whether the parent version of each version is also analyzed", defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private boolean includeParents;

    @CommandLine.Option(names = {"-n", "--project-name"}, paramLabel = "projectName", arity = "0..1", description = "Specifies the project's name (resolved by default from <repositoryPath>")
    private String projectNameOption;

    @CommandLine.Option(names = {"-f", "--folder"}, arity = "0..1", description = "Specifies the folder on which the analyzer will run (by default, it will run in <repositoryPath>).")
    private String folder;

    @Override
    public void run() {
        versionFile = normalizePath(versionFile);
        executable = normalizePath(executable);
        repositoryPath = normalizePath(repositoryPath);

        if (folder == null) {
            folder = repositoryPath;
        } else {
            folder = normalizePath(folder);
        }
        outputPath = normalizePath(outputPath);

        final ProjectName projectName = new ProjectName(projectNameOption);
        projectName.resolve(repositoryPath);

        final RevisionRepo repository = new RevisionRepo(new SimpleCommitReader());
        final List<String> revisions = repository.load(versionFile);

        try(VCS vcs = VcsBuilder.create(repositoryPath)) {
            final Analyzer analyzer = new SourceMeter(executable, outputPath, projectName.toString(), folder);
            final Collector collector = new Collector(analyzer, vcs);

            final List<String> analysisTargets = new ArrayList<>();
            if(includeParents) {
                final Genealogy genealogy = new Genealogy(vcs);
                genealogy.addRevisions(revisions);
                analysisTargets.addAll(genealogy.getUniqueRevisions());
            } else {
                analysisTargets.addAll(revisions);
            }

            logger.info("Read {} distinct revisions", revisions.size());

            final long beginning = System.nanoTime();
            int i = 0;
            for (String revision : analysisTargets) {
                logger.info("Processing {} ({})", revision, ++i);
                collector.analyzeRevision(revision, repositoryPath);
            }
            final long elapsed = System.nanoTime() - beginning;
            logger.info("Analysis completed in {}", Duration.ofNanos(elapsed));

            vcs.restoreVersion();
        } catch (VcsNotFound e) {
            logger.error("Failed to access folder {}", folder);
        } catch (Exception e) {
            logger.error("An error occurred while accessing the folder", e);
        }
    }
}
