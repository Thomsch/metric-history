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

import ch.thomsch.converter.SourceMeterConverter;
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
            case "convert":
                processConvertCommand(args);
                break;

            case "collect":
                processCollectCommand(args);
                break;

            case "ancestry":
                processAncestryCommand(args);
                break;

            case "diff":
                processDiffCommand(args);
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

    public void processDiffCommand(String[] args) {
        verifyArguments(args, 4);

        String ancestryFile = normalizePath(args[1]);
        String rawFile = normalizePath(args[2]);
        String outputFile = normalizePath(args[3]);

        Ancestry ancestry = new Ancestry(null, null);
        try {
            ancestry.loadFromDisk(ancestryFile);
        } catch (IOException e) {
            logger.error("I/O error while reading ancestry file");
            return;
        }

        CSVParser parser;
        try {
            parser = new CSVParser(new FileReader(rawFile), getFormat().withSkipHeaderRecord());
        } catch (IOException e) {
            logger.error("I/O error while reading raw file: {}" + e.getMessage());
            return;
        }
        Raw model = Raw.load(parser);

        Difference difference = new Difference();
        try (CSVPrinter writer = new CSVPrinter(new FileWriter(outputFile), getFormat())) {
            difference.export(ancestry, model, writer);
        } catch (IOException e) {
            logger.error("I/O error with file {}", outputFile, e);
        }
    }

    public void processAncestryCommand(String[] args) {
        verifyArguments(args, 4);

        String revisionFile = normalizePath(args[1]);
        String repository = normalizePath(args[2]);
        String outputFile = normalizePath(args[3]);

        Ancestry ancestry;
        try {
            ancestry = new Ancestry(GitRepository.get(repository), new ZafeirisRefactoringMiner());
        } catch (IOException e) {
            throw new IllegalArgumentException("This repository doesn't have version control: " + repository);
        }
        ancestry.load(revisionFile);

        try (CSVPrinter writer = ancestry.getPrinter(outputFile)) {
            ancestry.export(writer);
        } catch (IOException e) {
            logger.error("I/O error with file {}", outputFile, e);
        }

    }

    public void processCollectCommand(String[] args) {
        verifyArguments(args, 6);

        String revisionFile = normalizePath(args[0]);
        String executable = normalizePath(args[1]);
        String project = normalizePath(args[2]);

        String repository = args[3];
        if (repository.equalsIgnoreCase("same")) {
            repository = project;
        } else {
            repository = normalizePath(repository);
        }

        String executableOutput = normalizePath(args[4]);
        String projectName = args[5];

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

    public void processConvertCommand(String[] args) {
        verifyArguments(args, 3);

        String inputFolder = normalizePath(args[1]);
        String outputFile = normalizePath(args[2]);

        SourceMeterConverter.convert(inputFolder, outputFile);
    }

    private String normalizePath(String arg) {
        return FilenameUtils.normalize(new File(arg).getAbsolutePath());
    }

    /**
     * Verify the number of arguments.
     *
     * @param args     the container of arguments
     * @param expected the number of arguments expected
     * @throws IllegalArgumentException if the number of arguments doesn't match the actual number of arguments
     */
    private void verifyArguments(String[] args, int expected) {
        if (args.length != expected) {
            throw new IllegalArgumentException("Incorrect number of arguments (" + args.length + ") expected " +
                    expected);
        }
    }

    public static void main(String[] args) {
        new Application().doMain(args);
    }
}
