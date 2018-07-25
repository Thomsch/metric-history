package ch.thomsch;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import ch.thomsch.converter.SourceMeterConverter;
import ch.thomsch.database.Database;
import ch.thomsch.database.MongoAdapter;
import ch.thomsch.export.Reporter;
import ch.thomsch.loader.ZafeirisRefactoringMiner;
import ch.thomsch.metric.Collector;
import ch.thomsch.metric.SourceMeter;
import ch.thomsch.model.Raw;
import ch.thomsch.versioncontrol.GitRepository;

import static ch.thomsch.model.Raw.getFormat;

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
            help();
            return;
        }

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
                help();
                break;
        }
    }

    /**
     * Prints usage instructions in the terminal
     */
    private void help() {
        System.out.println("Usage: metric-history <command> <parameters>...");
        System.out.println("Refer to the source code of file " + getClass().getSimpleName() + ".java at " +
                "https://github.com/Thomsch/metric-history for the full list of commands");
    }

    public void processCollectCommand(String[] args) {
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
            MetricHistory metricHistory = new MetricHistory(collector, new Reporter(), new ZafeirisRefactoringMiner());

            metricHistory.collect(revisionFile, GitRepository.get(repository), "./output.csv");
        } catch (IOException e) {
            logger.error("Resource access problem", e);
        } catch (Exception e) {
            logger.error("Something went wrong", e);
        }
    }

    public void processAncestryCommand(String[] args) {
        atLeast(4, args);

        String revisionFile = normalizePath(args[1]);
        String repository = normalizePath(args[2]);
        String outputFile = normalizePath(args[3]);

        Ancestry ancestry;
        try {
            ancestry = new Ancestry(GitRepository.get(repository), new ZafeirisRefactoringMiner());
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
        atLeast(3, args);

        String inputFolder = normalizePath(args[1]);
        String outputFile = normalizePath(args[2]);

        SourceMeterConverter.convert(inputFolder, outputFile);
    }

    public void processDiffCommand(String[] args) {
        atLeast(4, args);

        String ancestryFile = normalizePath(args[1]);
        String rawFile = normalizePath(args[2]);
        String outputFile = normalizePath(args[3]);

        HashMap<String, String> ancestry = Ancestry.load(ancestryFile);
        if (ancestry.isEmpty()) {
            return;
        }

        CSVParser parser = rawParser(rawFile);
        if (parser == null) return;
        Raw model = Raw.load(parser);

        Difference difference = new Difference();
        try (CSVPrinter writer = new CSVPrinter(new FileWriter(outputFile), getFormat())) {
            difference.export(ancestry, model, writer);
        } catch (IOException e) {
            logger.error("I/O error with file {}", outputFile, e);
        }
    }

    public void processMongoCommand(String[] args) {
        atLeast(2, args);

        String connectionString = null;
        if (args.length == 5) {
            connectionString = args[4];
        }

        String databaseName = null;
        if (args.length >= 4) {
            databaseName = args[3];
        }

        Database database;
        Raw data;
        CSVParser parser;
        switch (args[1]) {
            case "raw":
                atLeast(4, args);

                parser = rawParser(args[2]);
                if (parser == null) return;

                data = Raw.load(parser);

                database = new MongoAdapter(connectionString, databaseName);
                database.setRaw(data);
                break;

            case "diff":
                atLeast(4, args);

                parser = rawParser(args[2]);
                if (parser == null) return;

                data = Raw.load(parser);
                database = new MongoAdapter(connectionString, databaseName);
                database.setDiff(data);
                break;

            case "ancestry":
                atLeast(4, args);

                String ancestryFile = normalizePath(args[2]);

                HashMap<String, String> ancestry = Ancestry.load(ancestryFile);
                if (ancestry.isEmpty()) {
                    logger.warn("No ancestry was found...");
                    return;
                }

                database = new MongoAdapter(connectionString, databaseName);
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

    private CSVParser rawParser(String rawFile) {
        CSVParser parser;
        try {
            parser = new CSVParser(new FileReader(rawFile), getFormat().withSkipHeaderRecord());
        } catch (IOException e) {
            logger.error("I/O error while reading raw file: {}" + e.getMessage());
            return null;
        }
        return parser;
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
