package ch.thomsch.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import ch.thomsch.metric.Collector;
import ch.thomsch.metric.MetricHistory;
import ch.thomsch.metric.SourceMeter;
import ch.thomsch.storage.export.Reporter;
import ch.thomsch.storage.loader.RefactoringMiner;
import ch.thomsch.versioncontrol.GitVCS;

/**
 * Analyze and build the RAW file for a single version of the project.
 */
public class Snapshot extends Command {

    private static final Logger logger = LoggerFactory.getLogger(Snapshot.class);

    private String commitId;
    private String executable;
    private String project;
    private String executableOutput;
    private String projectName;
    private String repository;

    @Override
    public String getName() {
        return "snapshot";
    }

    @Override
    public boolean parse(String[] parameters) {
        if (parameters.length < 6) {
            return false;
        }

        commitId = parameters[0];
        executable = normalizePath(parameters[1]);
        project = normalizePath(parameters[2]);

        repository = parameters[3];
        if (repository.equalsIgnoreCase("same")) {
            repository = project;
        } else {
            repository = normalizePath(repository);
        }

        executableOutput = normalizePath(parameters[4]);
        projectName = parameters[5];

        return true;
    }

    @Override
    public void execute() {
        try {
            final Collector collector = new SourceMeter(executable, executableOutput, projectName, project);
            final MetricHistory metricHistory = new MetricHistory(collector, new Reporter(), new RefactoringMiner());

            String outputFilePath = executableOutput + File.separator + commitId + ".csv";
            String collectorOutputDirectory = executableOutput + File.separator + projectName;

            metricHistory.collectRevision(commitId, GitVCS.get(repository),
                    outputFilePath, collectorOutputDirectory);
        } catch (IOException e) {
            logger.error("Resource access problem", e);
        } catch (Exception e) {
            logger.error("Something went wrong", e);
        }
    }

    @Override
    public void printUsage() {
        System.out.println("Usage: metric-history snapshot <commitId> <executable path> <project path> " +
                "<repository path> <output dir> <project name>");
        System.out.println();
        System.out.println("<commitId>     is the commit id of the project revision to be analyzed.");
        System.out.println("<executable path>   is the path to the executable to collect metrics.");
        System.out.println("<project path>      is the path to the folder containing the source code or the " +
                "project.");
        System.out.println("<repository path>   is the path to the folder containing .git folder. It can also be " +
                "set to 'same' if it's the same as <project path>.");
        System.out.println("<output dir>        is the path to the folder where the results should be extracted.");
        System.out.println("<project name>      is the name of the project.");
        System.out.println("Output: commitId.csv with collected metrics inside the <output dir>");
    }
}
