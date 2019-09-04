package org.metrichistory.cmd;

import org.metrichistory.analyzer.Analyzer;
import org.metrichistory.analyzer.AnalyzerBuilder;
import org.metrichistory.analyzer.SourceMeterConverter;
import org.metrichistory.cmd.util.ProjectName;
import org.metrichistory.mining.Collector;
import org.metrichistory.model.Genealogy;
import org.metrichistory.storage.loader.CommitReader;
import org.metrichistory.storage.loader.SimpleCommitReader;
import org.metrichistory.versioncontrol.VCS;
import org.metrichistory.versioncontrol.VcsBuilder;
import org.metrichistory.versioncontrol.VcsNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import picocli.CommandLine;

/**
 * Execute a code analyzer for multiple versions and their parents. The results are written on disk.
 */
@CommandLine.Command(
        name = "collect",
        description = "Analyzes one or more version from a project.")
public class Collect extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Collect.class);

    @CommandLine.Parameters(index = "0", paramLabel = "VERSIONS", description = "The path to the file containing the versions to analyse or if the SHA of a commit is specified, a snapshot will be performed instead.")
    private String versionsParam;

    @CommandLine.Parameters(index = "1", paramLabel = "REPOSITORY", description = "Path to the project's repository.")
    private String repositoryPath;

    @CommandLine.Parameters(index = "2", paramLabel = "OUTPUT", description = "Path of the folder that will contain the results.")
    private String outputPath;

    @CommandLine.Parameters(index = "3", paramLabel = "EXECUTABLE", description = "Analyzer to use. Valid values: ${COMPLETION-CANDIDATES}")
    private AnalyzerBuilder.Census analyzer;

    @CommandLine.Option(names = {"-e", "--exec-path"}, paramLabel = "EXECUTABLE PATH", description = "Path to the third-party analyzer.")
    private String executable;

    @CommandLine.Option(names = {"-p", "--include-parents"}, arity = "0..1", description = "Specifies whether the parent version of each version is also analyzed.", defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private boolean includeParents;

    @CommandLine.Option(names = {"-n", "--project-name"}, paramLabel = "projectName", arity = "0..1", description = "Specifies the project's name (resolved by default from <repositoryPath>.")
    private String projectNameOption;

    @CommandLine.Option(names = {"-f", "--folder"}, arity = "0..1", description = "Specifies the folder on which the analyzer will run (by default, it will run in <repositoryPath>).")
    private String folder;

    @Override
    public void run() {
        final Set<String> versions = loadRevisions(versionsParam);
        logger.info("Read {} distinct revisions", versions.size());

        repositoryPath = normalizePath(repositoryPath);
        outputPath = normalizePath(outputPath);
        executable = normalizePath(executable);
        folder = resolveInputFolder();

        final ProjectName projectName = new ProjectName(projectNameOption);
        projectName.resolve(repositoryPath);

        if(isSingleVersion(versionsParam)) {
            doASnapshot(projectName);
            return;
        }

        try(VCS vcs = VcsBuilder.create(repositoryPath)) {
            final Analyzer analyzer = initializeAnalyzer(projectName);
            final Collector collector = new Collector(analyzer, vcs);

            final List<String> analysisTargets = new ArrayList<>();
            if(includeParents) {
                final Genealogy genealogy = new Genealogy(vcs);
                genealogy.addRevisions(new ArrayList<>(versions));
                analysisTargets.addAll(genealogy.getUniqueRevisions());
            } else {
                analysisTargets.addAll(versions);
            }

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
            logger.error("Failed to access the repository {}", repositoryPath);
        } catch (Exception e) {
            logger.error("An error occurred while accessing the repository", e);
        }
    }

    private Set<String> loadRevisions(String versionsParam) {
        if (isSingleVersion(versionsParam)) {
            return Collections.singleton(versionsParam);
        } else {
            versionsParam = normalizePath(versionsParam);
            logger.info("Loading {}", versionsParam);
            final Set<String> versions = new HashSet<>();
            try {
                final CommitReader reader = new SimpleCommitReader();
                versions.addAll(reader.make(versionsParam));
            } catch (FileNotFoundException e) {
                System.err.println(String.format("File '%s' cannot be found.", versionsParam));
                System.exit(0);
            } catch (IOException e) {
                System.err.println(String.format("File '%s' cannot be parsed", versionsParam));
                System.exit(0);
            }
            return versions;
        }
    }

    private String resolveInputFolder() {
        if (folder == null) {
            return repositoryPath;
        } else {
            return normalizePath(folder);
        }
    }

    private Analyzer initializeAnalyzer(ProjectName projectName) {
        final AnalyzerBuilder analyzerBuilder = new AnalyzerBuilder();
        analyzerBuilder.setProjectName(projectName.toString());
        analyzerBuilder.setInputDirectory(folder);
        analyzerBuilder.setOutputDirectory(outputPath);
        analyzerBuilder.setExecutable(executable);
        return analyzerBuilder.build(analyzer);
    }

    private void doASnapshot(ProjectName projectName) {
        try (VCS vcs = VcsBuilder.create(repositoryPath)){
            final Analyzer analyzer = initializeAnalyzer(projectName);
            final Collector collector = new Collector(analyzer, vcs);

            final String outputFilePath = outputPath + File.separator + versionsParam + ".csv";
            final String collectorOutputDirectory = outputPath + File.separator + projectName;

            logger.info("Output file: {}", outputFilePath);
            logger.info("Processing single revision {}", versionsParam);

            final long beginning = System.nanoTime();
            try {
                collector.analyzeRevision(versionsParam, folder);
                SourceMeterConverter.convert(collectorOutputDirectory, outputFilePath);

                vcs.clean();
                vcs.close();
            } catch (IOException e) {
                logger.error("Resource access problem", e);
            } finally {
                final long elapsed = System.nanoTime() - beginning;
                logger.info("Snapshot completed in {}", Duration.ofNanos(elapsed));
            }
        } catch (VcsNotFound vcsNotFound) {
            logger.error("Failed to access the repository {}", repositoryPath);
        } catch (Exception e) {
            logger.error("An error occurred while accessing the repository", e);
        }
    }

    private boolean isSingleVersion(String versionsParam) {
        return Pattern.matches("^[a-f|0-9]{40}$", versionsParam);
    }
}
