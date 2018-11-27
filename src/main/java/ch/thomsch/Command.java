package ch.thomsch;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import ch.thomsch.metric.Collector;
import ch.thomsch.metric.MetricHistory;
import ch.thomsch.metric.SourceMeter;
import ch.thomsch.metric.SourceMeterConverter;
import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;
import ch.thomsch.storage.Database;
import ch.thomsch.storage.DatabaseBuilder;
import ch.thomsch.storage.RefactoringDetail;
import ch.thomsch.storage.Stores;
import ch.thomsch.storage.export.Reporter;
import ch.thomsch.storage.loader.RefactoringMiner;
import ch.thomsch.versioncontrol.GitRepository;

abstract class Command {

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
    abstract void execute() throws Exception;

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
        public void execute() throws IOException {
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

    public static class Tradeoff extends Command {
        private String ancestryFile;
        private String rawFile;
        private String refactorings;
        private String outputFile;
        private String mode;

        @Override
        public String getName() {
            return "tradeoffs";
        }

        @Override
        public boolean parse(String[] parameters) {
            if (parameters.length != 3) {
                return false;
            }

            refactorings = normalizePath(parameters[0]);
            ancestryFile = normalizePath(parameters[1]);
            rawFile = normalizePath(parameters[2]);
            return true;
        }

        @Override
        public void execute() throws IOException {
            final HashMap<String, String> ancestry = ch.thomsch.Ancestry.load(ancestryFile);
            if (ancestry.isEmpty()) return;

            ClassStore model = null;
            try {
                model = Stores.loadClasses(rawFile);
            } catch (IOException e) {
                logger.error("I/O error while reading file {}", rawFile);
            }

            final HashMap<String, RefactoringDetail> detailedRefactorings = new HashMap<>();
            try (CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().withDelimiter(';').parse(new FileReader(refactorings))) {
                for (CSVRecord record : parser) {
                    final String revision = record.get(0);
                    final String refactoringType = record.get(1);
                    final String description = record.get(2);

                    RefactoringDetail detail = detailedRefactorings.get(revision);
                    if(detail == null) {
                        detail = new RefactoringDetail();
                        detailedRefactorings.put(revision, detail);
                    }
                    detail.addRefactoring(refactoringType, description);
                }
            }catch (FileNotFoundException e) {
                logger.error("The file " + ancestryFile + " doesn't exists", e);
            } catch (IOException e) {
                logger.error("Error while reading the file " + ancestryFile);
            }

            final ClassStore finalModel = model;

            final HashMap<String, Metrics> results = new HashMap<>();
            final ch.thomsch.fluctuation.Difference diff = new ch.thomsch.fluctuation.Difference();

            detailedRefactorings.forEach((revision, refactoringDetail) ->
            {
                final List<Metrics> relevantMetrics = new ArrayList<>();
                for (String className : refactoringDetail.getClasses()) {
                    System.out.println(String.format("Parent: %s, Revision: %s", ancestry.get(revision), revision));
                    final Metrics revisionMetrics = finalModel.getMetric(revision, className);
                    final Metrics parentMetrics = finalModel.getMetric(ancestry.get(revision), className);

                    if (revisionMetrics != null && parentMetrics != null) {
                        final Metrics result = diff.computes(parentMetrics, revisionMetrics);
                        relevantMetrics.add(result);
                    }
                }

                if(!relevantMetrics.isEmpty()) {
                    results.put(revision, sum(relevantMetrics));
                }
            });

            results.forEach((revision, metrics) -> System.out.println(String.format("%s,%s", revision, metrics.toString())));
        }

        private Metrics sum(List<Metrics> refactoringChange) {
            final int numMetrics = refactoringChange.get(0).size();
            final Double[] sum = new Double[numMetrics];
            for (int i = 0; i < sum.length; i++) {
                sum[i] = (double) 0;
            }

            for (Metrics metrics : refactoringChange) {
                for (int i = 0; i < metrics.size(); i++) {
                    sum[i] += metrics.get(i);
                }
            }
            return new Metrics(sum);
        }

        @Override
        void printUsage() {
            System.out.println("Usage: metric-history " + getName() + " <refactoring list> <ancestry file> <raw file>");
            System.out.println();
            System.out.println("<refactoring list>  is the path of the file containing each refactoring.");
            System.out.println("<ancestry file>     is the path of the file produced by 'ancestry' command.");
            System.out.println("<raw file>          is the path of the file produced by 'convert' command.");
        }
    }
}
