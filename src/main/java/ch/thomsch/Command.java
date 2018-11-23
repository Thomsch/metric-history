package ch.thomsch;

import ch.thomsch.storage.Stores;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import ch.thomsch.metric.SourceMeterConverter;
import ch.thomsch.metric.MetricHistory;
import ch.thomsch.storage.Database;
import ch.thomsch.storage.DatabaseBuilder;
import ch.thomsch.storage.export.Reporter;
import ch.thomsch.storage.loader.RefactoringMiner;
import ch.thomsch.metric.Collector;
import ch.thomsch.metric.SourceMeter;
import ch.thomsch.model.ClassStore;
import ch.thomsch.versioncontrol.GitRepository;

public abstract class Command {

    private static final Logger logger = LoggerFactory.getLogger(Command.class);

    /**
     * Returns the name of the command. This is also the string to use to call it in command line.
     * @return the name, in lowercase.
     */
    abstract String getName();

    /**
     * Parses the parameters for the command.
     * @param parameters the parameters to parse
     * @return <code>true</code> if the parameters could be parsed, <code>false</code> otherwise.
     */
    abstract boolean parse(String[] parameters);

    /**
     * Executes the command.
     */
    abstract void execute();

    /**
     * Prints the usage of the command on <code>System.out</code>.
     */
    abstract void printUsage();

    String normalizePath(String arg) {
        return FilenameUtils.normalize(new File(arg).getAbsolutePath());
    }

    public static class Help extends Command {
        private final Collection<Command> values;

        Help(Collection<Command> values) {
            this.values = Objects.requireNonNull(values);
        }

        @Override
        public String getName() {
            return "help";
        }

        @Override
        public boolean parse(String[] parameters) {
            return true; // No parameters for this command
        }

        @Override
        public void execute() {
            System.out.println("Usage: metric-history <command> <parameters>...");
            System.out.println("or metric-history <command> to learn more about a particular command.");
            System.out.println();
            System.out.println("Where <command> can be one of ");
            values.forEach(command -> System.out.println("- " + command.getName()));
        }

        @Override
        void printUsage() {
            execute();
        }
    }

    public static class Ancestry extends Command {
        private String revisionFile;
        private GitRepository repository;
        private String outputFile;

        @Override
        public String getName() {
            return "ancestry";
        }

        @Override
        public boolean parse(String[] parameters) {
            if (parameters.length < 3) {
                return false;
            }

            revisionFile = normalizePath(parameters[0]);
            try {
                repository = GitRepository.get(normalizePath(parameters[1]));
            } catch (IOException e) {
                System.out.println("This repository doesn't have version control: " + repository.getDirectory());
            }
            outputFile = normalizePath(parameters[2]);
            return true;
        }

        @Override
        public void execute() {
            final ch.thomsch.Ancestry ancestry = new ch.thomsch.Ancestry(repository, new RefactoringMiner());
            ancestry.make(revisionFile);

            try (CSVPrinter writer = ancestry.getPrinter(outputFile)) {
                ancestry.export(writer);
            } catch (IOException e) {
                logger.error("I/O error with file {}", outputFile, e);
            }
        }

        @Override
        void printUsage() {
            System.out.println("Usage: metric-history ancestry <revision file> <repository path> <output file> ");
            System.out.println();
            System.out.println("<revision file>     is the path to the file containing the revision to analyse.");
            System.out.println("<repository path>   is the path to the folder containing .git folder");
            System.out.println("<output file>       is the path of the file where the results will be stored.");
        }
    }

    public static class Snapshot extends Command {
        private String commitId;
        private String executable;
        private String project;
        private String executableOutput;
        private String projectName;
        private String repository;

        @Override
        String getName() {
            return "Snapshot";
        }

        @Override
        boolean parse(String[] parameters) {
            if (parameters.length < 6) {
                return false;
            }

            commitId = normalizePath(parameters[0]);
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
        void execute() {
            try {
                final Collector collector = new SourceMeter(executable, executableOutput, projectName, project);
                final MetricHistory metricHistory = new MetricHistory(collector, new Reporter(), new RefactoringMiner());

                metricHistory.collectRevision(commitId, GitRepository.get(repository), executableOutput + File.separator + commitId + ".csv");
            } catch (IOException e) {
                logger.error("Resource access problem", e);
            } catch (Exception e) {
                logger.error("Something went wrong", e);
            }
        }

        @Override
        void printUsage() {
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

    public static class Collect extends Command {
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
        void printUsage() {
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

    public static class Convert extends Command {
        private String inputFolder;
        private String outputFile;

        @Override
        public String getName() {
            return "convert";
        }

        @Override
        public boolean parse(String[] parameters) {
            if (parameters.length < 2) {
                return false;
            }

            inputFolder = normalizePath(parameters[0]);
            outputFile = normalizePath(parameters[1]);

            return true;
        }

        @Override
        public void execute() {
            SourceMeterConverter.convert(inputFolder, outputFile);
        }

        @Override
        void printUsage() {
            System.out.println("Usage: metric-history convert <folder> <output file> ");
            System.out.println();
            System.out.println("<folder>            is the path of the root folder containing the results from the " +
                    "third party tool");
            System.out.println("<output file>       is the path of the file where the results will be stored.");
        }
    }

    public static class Difference extends Command {
        private String ancestryFile;
        private String rawFile;
        private String outputFile;

        @Override
        public String getName() {
            return "diff";
        }

        @Override
        public boolean parse(String[] parameters) {
            if (parameters.length < 3) {
                return false;
            }

            ancestryFile = normalizePath(parameters[0]);
            rawFile = normalizePath(parameters[1]);
            outputFile = normalizePath(parameters[2]);

            return true;
        }

        @Override
        public void execute() {
            final HashMap<String, String> ancestry = ch.thomsch.Ancestry.load(ancestryFile);
            if (ancestry.isEmpty()) {
                return;
            }

            ClassStore model = null;
            try {
                model = Stores.loadClasses(rawFile);
            } catch (IOException e) {
                logger.error("I/O error while reading file {}", rawFile);
            }

            final ch.thomsch.fluctuation.Difference difference = new ch.thomsch.fluctuation.Difference();
            try (CSVPrinter writer = new CSVPrinter(new FileWriter(outputFile), Stores.getFormat())) {
                difference.export(ancestry, model, writer);
            } catch (IOException e) {
                logger.error("I/O error with file {}", outputFile, e);
            }
        }

        @Override
        void printUsage() {
            System.out.println("Usage: metric-history diff <ancestry file> <raw file> <output file>");
            System.out.println();
            System.out.println("<ancestry file>     is the path of the file produced by 'ancestry' command");
            System.out.println("<raw file>          is the path of the file produced by 'convert' command");
            System.out.println("<output file>       is the path of the file where the results will be stored.");
        }
    }

    public static class Mongo extends Command {
        private String action;
        private String file;
        private String databaseName;
        private String connectionString;

        @Override
        public String getName() {
            return "mongo";
        }

        @Override
        public boolean parse(String[] parameters) {
            if (parameters.length < 3) {
                return false;
            }

            action = parameters[0];
            this.file = normalizePath(parameters[1]);
            databaseName = parameters[2];
            connectionString = parameters.length == 4 ? parameters[3] : null;

            return true;
        }

        @Override
        public void execute() {
            final Database database = DatabaseBuilder.build(connectionString, databaseName);

            try {
                switch (action) {
                    case "raw":
                        final ClassStore raw = Stores.loadClasses(file);
                        database.setRaw(raw);
                        break;

                    case "diff":
                        final ClassStore diff = Stores.loadClasses(file);
                        database.setDiff(diff);
                        break;

                    case "ancestry":
                        final HashMap<String, String> ancestry = ch.thomsch.Ancestry.load(file);

                        if (ancestry.isEmpty()) {
                            logger.warn("No ancestry was found...");
                            return;
                        }

                        database.persist(ancestry);
                        break;

                    default:
                        printUsage();
                        break;
                }
            } catch (IOException e) {
                logger.error("I/O error with file {}", file, e);
            }
        }

        @Override
        void printUsage() {
            System.out.println("Usages:");
            System.out.println("     metric-history mongo raw <raw file> <storage name> [remote URI]");
            System.out.println("     metric-history mongo diff <diff file> <storage name> [remote URI]");
            System.out.println("     metric-history mongo ancestry <ancestry file> <storage name> [remote URI]");
        }
    }
}
