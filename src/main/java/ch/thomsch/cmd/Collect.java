package ch.thomsch.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import ch.thomsch.metric.Collector;
import ch.thomsch.metric.MetricHistory;
import ch.thomsch.metric.SourceMeter;
import ch.thomsch.storage.export.Reporter;
import ch.thomsch.storage.loader.RefactoringMiner;
import ch.thomsch.versioncontrol.GitRepository;

/**
 *
 */
public class Collect extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Collect.class);

    private String revisionFile;
    private String executable;
    private String project;
    private String executableOutput;
    private String projectName;
    private String repository;

    @Override
    public String getName() {
        return "collect";
    }

    @Override
    public boolean parse(String[] parameters) {
        if (parameters.length < 6) {
            return false;
        }

        revisionFile = normalizePath(parameters[0]);
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

            metricHistory.collect(revisionFile, GitRepository.get(repository), "./output.csv");
        } catch (IOException e) {
            logger.error("Resource access problem", e);
        } catch (Exception e) {
            logger.error("Something went wrong", e);
        }
    }

    @Override
    public void printUsage() {
        System.out.println("Usage: metric-history collect <revision file> <executable path> <project path> " +
                "<repository path> <output dir> <project name>");
        System.out.println();
        System.out.println("<revision file>     is the path to the file containing the revision to analyse.");
        System.out.println("<executable path>   is the path to the executable to collect metrics.");
        System.out.println("<project path>      is the path to the folder containing the source code or the " +
                "project.");
        System.out.println("<repository path>   is the path to the folder containing .git folder. It can also be " +
                "set to 'same' if it's the same as <project path>.");
        System.out.println("<output dir>        is the path to the folder where the results should be extracted.");
        System.out.println("<project name>      is the name of the project.");
    }
}
