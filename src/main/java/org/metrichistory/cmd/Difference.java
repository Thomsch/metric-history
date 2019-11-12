package org.metrichistory.cmd;

import org.metrichistory.cmd.util.ProgressIndicator;
import org.metrichistory.fluctuation.*;
import org.metrichistory.mining.VersionComparator;
import org.metrichistory.model.MeasureStore;
import org.metrichistory.storage.GenealogyRepo;
import org.metrichistory.storage.MeasureRepository;
import org.metrichistory.storage.SaveTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Computes the metric fluctuations from file(s) in RAW format.
 */
@CommandLine.Command(
        name = "diff",
        description = "Computes the metric fluctuations from file(s) in RAW format.")
public class Difference extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Difference.class);

    @CommandLine.Parameters(index = "0", description = "Path of the file produced by 'ancestry' command.")
    private String ancestryFile;

    @CommandLine.Parameters(index = "1", description = "Path of the file or directory produced by 'convert' command.")
    private String input;

    @CommandLine.Parameters(index = "2", description = "Path of the file where the results will be stored or a directory that will contain one file per revision.")
    private String output;

    @CommandLine.Option(names = {"-c", "--classes"}, arity = "0..1",
            description = "Specifies which kind of classes to take into account.  Valid values: ${COMPLETION-CANDIDATES}",
            defaultValue = "ALL", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private On onOption;

    @CommandLine.Option(names = {"-h", "--how"}, arity = "0..1",
            description = "Specifies how to make the difference.  Valid values: ${COMPLETION-CANDIDATES}",
            defaultValue = "ABSOLUTE", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private How howOption;

    @Override
    public void run() {
        ancestryFile = normalizePath(ancestryFile);
        input = normalizePath(input);
        output = normalizePath(output);
        final Computer computer = buildComputer(onOption, howOption);

        try {
            execute(computer);
        } catch (IOException e) {
            final String errorMessage = String.format("The ancestry file (%s) cannot be read", ancestryFile);
            System.err.println(errorMessage);
            logger.error(errorMessage, e);
        }
    }

    private void execute(Computer computer) throws IOException {
        final GenealogyRepo repo = new GenealogyRepo();
        final HashMap<String, String> ancestry = repo.load(ancestryFile);
        if (ancestry.isEmpty()) {
            return;
        }

        final SaveTarget outputSink = SaveTarget.build(output);
        if(outputSink == null) {
            return;
        }

        final MeasureRepository measureRepository = MeasureRepository.build(input);
        final VersionComparator versionComparator = new VersionComparator(computer);

        final LinkedList<Map.Entry<String, String>> entries = new LinkedList<>(ancestry.entrySet());
        final ProgressIndicator progressIndicator = new ProgressIndicator(entries.size(), 5);
        final LinkedList<Error> errors = new LinkedList<>();

        entries.forEach(entry -> {
            final String version = entry.getKey();
            final String parent = entry.getValue();

            try {
                final MeasureStore measures = measureRepository.get(version, parent);
                final MeasureStore measureStore = versionComparator.fluctuations(version, parent, measures);
                outputSink.export(measureStore);
            } catch (Exception e) {
                errors.add(new Error(version, parent, e));
            } finally {
                progressIndicator.update();
            }
        });

        if(errors.size() > 0) {
            System.err.println(String.format("The command terminated with %d error%s, see the logs for more details.", errors.size(), errors.size() > 1 ? "s" : ""));
            logger.error("{} errors occurred during processing:", errors.size());
            errors.forEach(Error::display);
        }
    }

    private Computer buildComputer(On onOption, How howOption) {
        Objects.requireNonNull(onOption);

        final BiFunction<Double, Double, Double> computeDifference = buildComputationMethod(howOption);

        switch (onOption){
            case ALL:
                return new AllChanges(computeDifference);
            case CHANGES:
                return new UpdateChanges(computeDifference);
            default:
                throw new IllegalStateException("Unable to process the class target parameter");
        }
    }

    private BiFunction<Double, Double, Double> buildComputationMethod(How howOption) {
        switch (howOption) {
            case RELATIVE:
                return new RelativeChange();
            case ABSOLUTE:
                return new AbsoluteChange();
            default:
                throw new IllegalStateException(String.format("The change computation method '%s' is not recognized", howOption));
        }
    }

    private static class Error {
        final String version;
        final String parent;
        final Exception e;

        Error(String version, String parent, Exception e) {
            this.version = version;
            this.parent = parent;
            this.e = e;
        }

        void display() {
            logger.error("At {} (w. parent {}):", version, parent, e);
        }
    }

    private enum On {ALL, CHANGES}
    private enum How {RELATIVE, ABSOLUTE}
}
