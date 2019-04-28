package org.metrichistory.cmd;

import org.metrichistory.mining.VersionComparator;
import org.metrichistory.model.MeasureStore;
import org.metrichistory.storage.MeasureRepository;
import org.metrichistory.storage.SaveTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.metrichistory.storage.GenealogyRepo;

import picocli.CommandLine;

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

    @Override
    public void run() {
        ancestryFile = normalizePath(ancestryFile);
        input = normalizePath(input);
        output = normalizePath(output);

        try {
            execute();
        } catch (Exception e) {
            logger.error("An error occurred:", e);
        }
    }

    private void execute() throws IOException {
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
        final VersionComparator versionComparator = new VersionComparator();


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
            logger.error("{} errors occurred during processing:", errors.size());
            errors.forEach(Error::display);
        }
    }

    private class Error {
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
}
