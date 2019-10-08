package org.metrichistory.cmd;

import org.metrichistory.analyzer.Analyzer;
import org.metrichistory.analyzer.AnalyzerBuilder;
import org.metrichistory.analyzer.sourcemeter.SourceMeterConverter;
import org.metrichistory.cmd.util.ProjectNameResolver;
import org.metrichistory.mining.Collector;
import org.metrichistory.model.Genealogy;
import org.metrichistory.storage.CommitReader;
import org.metrichistory.storage.SimpleCommitReader;
import org.metrichistory.versioncontrol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

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

    @CommandLine.Parameters(index = "3", paramLabel = "ANALYZER", description = "Analyzer to use. Valid values: ${COMPLETION-CANDIDATES}")
    private AnalyzerBuilder.Census analyzer;

    @CommandLine.Option(names = {"-e", "--exec-path"}, paramLabel = "EXECUTABLE PATH", description = "Path to the third-party analyzer.")
    private String executable;

    @CommandLine.Option(names = {"-p", "--include-parents"}, arity = "0..1", description = "Specifies whether the parent version of each version is also analyzed.", defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private boolean includeParents;

    @CommandLine.Option(names = {"-n", "--project-name"}, paramLabel = "projectName", arity = "0..1", description = "Specifies the project's name (resolved by default from <repositoryPath>.")
    private String projectNameOption;

    @CommandLine.Option(names = {"-f", "--folder"}, arity = "0..1", description = "Specifies the folder on which the analyzer will run (by default, it will run in <repositoryPath>).")
    private String folderOption;

    @Override
    public void run() {
        final Set<String> versions = retrieveVersions(versionsParam);
        if(versions.size() == 0) {
            return;
        }
        logger.info("Read {} distinct revisions", versions.size());

        repositoryPath = normalizePath(repositoryPath);
        outputPath = normalizePath(outputPath);
        executable = normalizePath(executable);
        final String folder = Optional.ofNullable(folderOption).orElse(repositoryPath);
        final String projectName = Optional.ofNullable(projectNameOption).orElseGet(new ProjectNameResolver(repositoryPath));

        // Snapshot mode is only activated by specifying one version inline.
        if(isSingleVersion(versionsParam)) {
            doASnapshot(projectName, folder);
            return;
        }

        try {
            final List<String> versionsToAnalyze = retrieveVersionsToAnalyze(versions, repositoryPath, includeParents);
            final Analyzer analyzer = buildAnalyzer(projectName, folder);
            final Collector collector = new Collector(analyzer);

            collector.analyzeVersions(versionsToAnalyze, repositoryPath);
        } catch (VcsNotFound e) {
            System.err.println(String.format("The repository at '%s' cannot be found", repositoryPath));
            logger.error("Failed to access the repository {}", repositoryPath);
        } catch (VcsCleanupException e) {
            e.printStackTrace();
            logger.error("Failed to cleanup the repository", e);
        } catch (VcsOperationException e) {
            e.printStackTrace();
            logger.error("The version control system encountered an error {}", e.getMessage());
        }
    }

    private List<String> retrieveVersionsToAnalyze(Set<String> versions, String repositoryPath, boolean includeParents) throws VcsNotFound {
        final List<String> result = new ArrayList<>();

        if(includeParents) {
            final Genealogy genealogy = new Genealogy(VcsBuilder.create(repositoryPath));
            genealogy.addRevisions(new ArrayList<>(versions));
            result.addAll(genealogy.getUniqueRevisions());
        } else {
            result.addAll(versions);
        }
        return result;
    }

    private Set<String> retrieveVersions(String versionsParam) {
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

    private Analyzer buildAnalyzer(String projectName, String folder) {
        final AnalyzerBuilder analyzerBuilder = new AnalyzerBuilder();
        analyzerBuilder.setProjectName(projectName);
        analyzerBuilder.setInputDirectory(folder);
        analyzerBuilder.setOutputDirectory(outputPath);
        analyzerBuilder.setExecutable(executable);
        return analyzerBuilder.build(analyzer);
    }

    private void doASnapshot(String projectName, String folder) {
        try (VCS vcs = VcsBuilder.create(repositoryPath)){
            final Analyzer analyzer = buildAnalyzer(projectName, folder);
            final Collector collector = new Collector(analyzer);

            final String outputFilePath = outputPath + File.separator + versionsParam + ".csv";
            final String collectorOutputDirectory = outputPath + File.separator + projectName;

            logger.info("Output file: {}", outputFilePath);
            logger.info("Processing single revision {}", versionsParam);

            final long beginning = System.nanoTime();
            try {
                collector.analyzeVersion(versionsParam, folder);
                vcs.clean();
                vcs.close();

                SourceMeterConverter.convert(collectorOutputDirectory, outputFilePath);
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
