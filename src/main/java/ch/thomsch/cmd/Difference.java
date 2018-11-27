package ch.thomsch.cmd;

import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import ch.thomsch.model.ClassStore;
import ch.thomsch.storage.Stores;

/**
 *
 */
public class Difference extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Difference.class);

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
    public void printUsage() {
        System.out.println("Usage: metric-history diff <ancestry file> <raw file> <output file>");
        System.out.println();
        System.out.println("<ancestry file>     is the path of the file produced by 'ancestry' command");
        System.out.println("<raw file>          is the path of the file produced by 'convert' command");
        System.out.println("<output file>       is the path of the file where the results will be stored.");
    }
}
