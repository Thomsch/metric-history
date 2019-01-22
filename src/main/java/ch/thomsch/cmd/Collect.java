package ch.thomsch.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import ch.thomsch.metric.Analyzer;
import ch.thomsch.metric.Collector;
import ch.thomsch.metric.SourceMeter;
import ch.thomsch.model.Genealogy;
import ch.thomsch.storage.RevisionRepo;
import ch.thomsch.storage.loader.SimpleCommitReader;
import ch.thomsch.versioncontrol.GitVCS;

/**
 * Execute a code analyzer for multiple versions and their parents. The results are written on disk.
 */
public class Collect extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Collect.class);

    private String revisionFile;
    private String executable;
    private String projectPath;
    private String outputPath;
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
        projectPath = normalizePath(parameters[2]);

        repository = parameters[3];
        if (repository.equalsIgnoreCase("same")) {
            repository = projectPath;
        } else {
            repository = normalizePath(repository);
        }

        outputPath = normalizePath(parameters[4]);
        projectName = parameters[5];

        return true;
    }

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
            collector.analyzeRevision(projectPath, revision);
        }
        final long elapsed = System.nanoTime() - beginning;
        logger.info("Collection completed in {}", Duration.ofNanos(elapsed));

        vcs.close();
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
