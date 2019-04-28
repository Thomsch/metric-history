package org.metrichistory.cmd;

import org.metrichistory.mining.Analyzer;
import org.metrichistory.mining.Collector;
import org.metrichistory.mining.SourceMeter;
import org.metrichistory.mining.SourceMeterConverter;
import org.metrichistory.versioncontrol.VcsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.metrichistory.versioncontrol.VCS;

import picocli.CommandLine;

/**
 * Analyze and build the RAW file for a single version of the project.
 */
@CommandLine.Command(
        name = "snapshot",
        description = "Analyze and build the RAW file for a single version of the project.\nOutput: commitId.csv with collected metrics inside the <output dir>")
public class Snapshot extends Command {

    private static final Logger logger = LoggerFactory.getLogger(Snapshot.class);

    @CommandLine.Parameters(index = "0", description = "Commit id of the project revision to be analyzed.")
    private String commitId;

    @CommandLine.Parameters(index = "1", description = "Path to the executable to collect metrics.")
    private String executable;

    @CommandLine.Parameters(index = "2", description = "Path to the folder containing the source code or the project.")
    private String project;

    @CommandLine.Option(names = {"-r", "--repository"}, arity = "0..1", description = "Path to the folder containing .git folder. If omitted, will be searched in the project path.")
    private String repository;

    @CommandLine.Parameters(index = "3", description = "Path to the folder where the results should be extracted.")
    private String executableOutput;

    @CommandLine.Parameters(index = "4", description = "Name of the project.")
    private String projectName;

    @Override
    public void run() {
        executable = normalizePath(executable);
        project = normalizePath(project);

        if (repository == null) {
            repository = project;
        } else {
            repository = normalizePath(repository);
        }

        executableOutput = normalizePath(executableOutput);

        try {
            execute();
        } catch (Exception e) {
            logger.error("An error occurred:", e);
        }
    }

    private void execute() throws Exception {
        final Analyzer analyzer = new SourceMeter(executable, executableOutput, projectName, project);
        final VCS vcs = VcsBuilder.create(repository);
        final Collector collector = new Collector(analyzer, vcs);

        final String outputFilePath = executableOutput + File.separator + commitId + ".csv";
        final String collectorOutputDirectory = executableOutput + File.separator + projectName;

        logger.info("Output file: {}", outputFilePath);
        logger.info("Processing single revision {}", commitId);

        final long beginning = System.nanoTime();
        try {
            collector.analyzeRevision(commitId, project);
            SourceMeterConverter.convert(collectorOutputDirectory, outputFilePath);

            vcs.clean();
            vcs.close();
        } catch (IOException e) {
            logger.error("Resource access problem", e);
        } finally {
            final long elapsed = System.nanoTime() - beginning;
            logger.info("Snapshot completed in {}", Duration.ofNanos(elapsed));
        }
    }
}
