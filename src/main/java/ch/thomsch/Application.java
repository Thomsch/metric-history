package ch.thomsch;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import ch.thomsch.converter.SourceMeterConverter;
import ch.thomsch.csv.Stores;
import ch.thomsch.database.Database;
import ch.thomsch.database.DatabaseBuilder;
import ch.thomsch.export.Reporter;
import ch.thomsch.loader.RefactoringMiner;
import ch.thomsch.metric.Collector;
import ch.thomsch.metric.SourceMeter;
import ch.thomsch.model.ClassStore;
import ch.thomsch.versioncontrol.GitRepository;

/**
 * Entry point for the application.
 */
public final class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * Parse and execute from the command line.
     *
     * @param args the arguments of the command line.
     *             args[0] contains the action to execute.
     *             The remaining arguments are the parameters for the action.
     */
    void doMain(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        try {
            processSubCommand(args);
        } catch (IOException e) {
            logger.error("An I/O error occurred while reading or writing a file", e);
        }
    }

    private void processSubCommand(String[] args) throws IOException {
        switch (args[0]) {
            case "collect":
                processCollectCommand(args);
                break;

            case "ancestry":
                processAncestryCommand(args);
                break;

            case "convert":
                processConvertCommand(args);
                break;

            case "diff":
                processDiffCommand(args);
                break;

            case "mongo":
                processMongoCommand(args);
                break;

            default:
                System.out.println("Unknown command '" + args[0] + "'. Verify the spelling and make sure your command" +
                        " is in lowercase.");
                System.out.println();
                printHelp();
                break;
        }
    }

    /**
     * Prints usage instructions in the terminal
     */
    private void printHelp() {
        System.out.println("Usage: metric-history <command> <parameters>...");
        System.out.println("or metric-history <command> to learn more.");
        System.out.println();
        System.out.println("Where <command> = collect|ancestry|convert|diff|mongo");
    }

    public void processCollectCommand(String[] args) {
        if (args.length == 1) {
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
            return;
        }

        atLeast(7, args);

        String revisionFile = normalizePath(args[1]);
        String executable = normalizePath(args[2]);
        String project = normalizePath(args[3]);

        String repository = args[4];
        if (repository.equalsIgnoreCase("same")) {
            repository = project;
        } else {
            repository = normalizePath(repository);
        }

        String executableOutput = normalizePath(args[5]);
        String projectName = args[6];

        try {
            Collector collector = new SourceMeter(executable, executableOutput, projectName, project);
            MetricHistory metricHistory = new MetricHistory(collector, new Reporter(), new RefactoringMiner());

            metricHistory.collect(revisionFile, GitRepository.get(repository), "./output.csv");
        } catch (IOException e) {
            logger.error("Resource access problem", e);
        } catch (Exception e) {
            logger.error("Something went wrong", e);
        }
    }

    public void processAncestryCommand(String[] args) {
        if (args.length == 1) {
            System.out.println("Usage: metric-history ancestry <revision file> <repository path> <output file> ");
            System.out.println();
            System.out.println("<revision file>     is the path to the file containing the revision to analyse.");
            System.out.println("<repository path>   is the path to the folder containing .git folder");
            System.out.println("<output file>       is the path of the file where the results will be stored.");
            return;
        }

        atLeast(4, args);

        String revisionFile = normalizePath(args[1]);
        String repository = normalizePath(args[2]);
        String outputFile = normalizePath(args[3]);

        Ancestry ancestry;
        try {
            ancestry = new Ancestry(GitRepository.get(repository), new RefactoringMiner());
        } catch (IOException e) {
            throw new IllegalArgumentException("This repository doesn't have version control: " + repository);
        }
        ancestry.make(revisionFile);

        try (CSVPrinter writer = ancestry.getPrinter(outputFile)) {
            ancestry.export(writer);
        } catch (IOException e) {
            logger.error("I/O error with file {}", outputFile, e);
        }
    }

    public void processConvertCommand(String[] args) {
        if (args.length == 1) {
            System.out.println("Usage: metric-history convert <folder> <output file> ");
            System.out.println();
            System.out.println("<folder>            is the path of the root folder containing the results from the " +
                    "third party tool");
            System.out.println("<output file>       is the path of the file where the results will be stored.");
            return;
        }
        atLeast(3, args);

        String inputFolder = normalizePath(args[1]);
        String outputFile = normalizePath(args[2]);

        SourceMeterConverter.convert(inputFolder, outputFile);
    }

    public void processDiffCommand(String[] args) throws IOException {
        if (args.length == 1) {
            System.out.println("Usage: metric-history diff <ancestry file> <raw file> <output file>");
            System.out.println();
            System.out.println("<ancestry file>     is the path of the file produced by 'ancestry' command");
            System.out.println("<raw file>          is the path of the file produced by 'convert' command");
            System.out.println("<output file>       is the path of the file where the results will be stored.");
            return;
        }
        atLeast(4, args);

        String ancestryFile = normalizePath(args[1]);
        String rawFile = normalizePath(args[2]);
        String outputFile = normalizePath(args[3]);

        HashMap<String, String> ancestry = Ancestry.load(ancestryFile);
        if (ancestry.isEmpty()) {
            return;
        }

        ClassStore model = Stores.loadClasses(rawFile);

        Difference difference = new Difference();
        try (CSVPrinter writer = new CSVPrinter(new FileWriter(outputFile), Stores.getFormat())) {
            difference.export(ancestry, model, writer);
        } catch (IOException e) {
            logger.error("I/O error with file {}", outputFile, e);
        }
    }

    public void processMongoCommand(String[] args) throws IOException {
        if (args.length <= 2) {
            printMongoUsage();
            return;
        }
        atLeast(4, args);

        final String action = args[1];
        final String file = normalizePath(args[2]);
        String databaseName = args[3];

        String connectionString = args.length == 5 ? args[4] : null;

        Database database = DatabaseBuilder.build(connectionString, databaseName);

        switch (action) {
            case "raw":
                ClassStore raw = Stores.loadClasses(file);
                database.setRaw(raw);
                break;

            case "diff":
                ClassStore diff = Stores.loadClasses(file);
                database.setDiff(diff);
                break;

            case "ancestry":
                HashMap<String, String> ancestry = Ancestry.load(file);

                if (ancestry.isEmpty()) {
                    logger.warn("No ancestry was found...");
                    return;
                }

                database.persist(ancestry);
                break;

            default:
                printMongoUsage();
                break;
        }
    }

    private void printMongoUsage() {
        System.out.println("Usages:");
        System.out.println("     metric-history mongo raw <raw file> <database name> [remote URI]");
        System.out.println("     metric-history mongo diff <diff file> <database name> [remote URI]");
        System.out.println("     metric-history mongo ancestry <ancestry file> <database name> [remote URI]");
    }

    private String normalizePath(String arg) {
        return FilenameUtils.normalize(new File(arg).getAbsolutePath());
    }

    /**
     * Verify the number of arguments.
     *
     * @param expected the number of arguments expected
     * @param args     the container of arguments
     * @throws IllegalArgumentException if the number of arguments doesn't match the actual number of arguments
     */
    private void atLeast(int expected, String[] args) {
        if (expected > args.length) {
            throw new IllegalArgumentException("Incorrect number of arguments (" + args.length + ") expected " +
                    expected);
        }
    }

    public static void main(String[] args) {
        new Application().doMain(args);
    }
}
