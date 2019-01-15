package ch.thomsch.cmd;

import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ch.thomsch.fluctuation.Differences;
import ch.thomsch.model.ClassStore;
import ch.thomsch.storage.Stores;

/**
 *
 */
public class Difference extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Difference.class);

    private String ancestryFile;
    private String rawFile;
    private String output;

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
        output = normalizePath(parameters[2]);

        return true;
    }

    @Override
    public void execute() throws IOException {
        final HashMap<String, String> ancestry = ch.thomsch.Ancestry.load(ancestryFile);
        if (ancestry.isEmpty()) {
            return;
        }

        final ClassStore model;
        try {
            model = Stores.loadClasses(rawFile);
        } catch (IOException e) {
            logger.error("I/O error while reading file {}", rawFile);
            return;
        }

        if(isFile(output)) {
            exportDiff(ancestry, model, new File(output));
        } else {
            final File outputDir = createDirectory(output);

            final LinkedList<Map.Entry<String, String>> entries = new LinkedList<>(ancestry.entrySet());
            entries.parallelStream().forEach(entry -> {
                final HashMap<String, String> map = new HashMap<>();
                map.put(entry.getKey(), entry.getValue());

                final File outputFile = new File(outputDir, entry.getKey() + ".csv");
                exportDiff(map, model, outputFile);
            });
        }
    }

    /**
     * Creates a new directory if it doesn't exists
     * @param output the path of the directory
     * @return a new instance of {@link File} representing the path of the directory
     * @throws IOException when the directory cannot be created
     */
    private File createDirectory(String output) throws IOException {
        final File outputDir = new File(output);
        if (!outputDir.exists()) {
            logger.info("Creating folder {}", outputDir);
            final boolean success = outputDir.mkdir();
            if (!success) {
                throw new IOException("Cannot create directory " + outputDir);
            }
        }
        return outputDir;
    }

    private void exportDiff(HashMap<String, String> map, ClassStore model, File outputFile) {
        try (CSVPrinter writer = new CSVPrinter(new FileWriter(outputFile), Stores.getFormat())) {
            Differences.export(map, model, writer);
        } catch (IOException e) {
            logger.error("I/O error with file {}", output, e);
        }
    }

    /**
     * Returns if the specified output location is a file or a directory. A directory is any name not containing a '.'.
     * Otherwise the path is considered a file.
     * @param output the canonical location of the output
     * @return true if the path is referring to a file
     */
    private boolean isFile(String output) {
        return new File(output).getName().contains(".");
    }

    @Override
    public void printUsage() {
        System.out.println("Usage: metric-history diff <ancestry file> <raw file> <output>");
        System.out.println();
        System.out.println("<ancestry file>     is the path of the file produced by 'ancestry' command");
        System.out.println("<raw file>          is the path of the file produced by 'convert' command");
        System.out.println("<output>            is the path of the file where the results will be stored or a directory that will contain one file per revision");
    }
}
