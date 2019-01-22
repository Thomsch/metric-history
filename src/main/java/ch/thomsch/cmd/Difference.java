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
import ch.thomsch.storage.DiskUtils;
import ch.thomsch.storage.GenealogyRepo;
import ch.thomsch.storage.Stores;

public class Difference extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Difference.class);

    private String ancestryFile;
    private String input;
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
        input = normalizePath(parameters[1]);
        output = normalizePath(parameters[2]);

        return true;
    }

    @Override
    public void execute() throws IOException {
        final GenealogyRepo repo = new GenealogyRepo();
        final HashMap<String, String> ancestry = repo.load(ancestryFile);
        if (ancestry.isEmpty()) {
            return;
        }

        if(DiskUtils.isFile(input)) {
            final ClassStore model = new ClassStore();
            try {
                Stores.loadLargeClasses(input, model);
            } catch (IOException e) {
                logger.error("I/O error while reading file {}", input);
                return;
            }
            export(ancestry, model);
        } else if (DiskUtils.isFile(output)) {
            logger.info("Multi-input to output one file is not supported.");
        } else {
            final File inputDir = new File(input);
            final File outputDir = DiskUtils.createDirectory(output);
            final LinkedList<Map.Entry<String, String>> entries = new LinkedList<>(ancestry.entrySet());
            entries.forEach(entry -> {
                final ClassStore model = new ClassStore();
                final String revision = entry.getKey();
                final String parent = entry.getValue();

                final HashMap<String, String> map = new HashMap<>();
                map.put(entry.getKey(), entry.getValue());

                try {
                    Stores.loadClasses(new File(inputDir, revision + ".csv").getPath(), model);
                    Stores.loadClasses(new File(inputDir, parent + ".csv").getPath(), model);

                    exportDiff(map, model, new File(outputDir, revision + ".csv"));
                } catch (IOException e) {
                    logger.error("Failed to access file", e);
                }
            });
        }
    }

    private void export(HashMap<String, String> ancestry, ClassStore model) throws IOException {
        if(DiskUtils.isFile(output)) {
            exportDiff(ancestry, model, new File(output));
        } else {
            final File outputDir = DiskUtils.createDirectory(output);

            final LinkedList<Map.Entry<String, String>> entries = new LinkedList<>(ancestry.entrySet());
            entries.parallelStream().forEach(entry -> {
                final HashMap<String, String> map = new HashMap<>();
                map.put(entry.getKey(), entry.getValue());

                final File outputFile = new File(outputDir, entry.getKey() + ".csv");
                exportDiff(map, model, outputFile);
            });
        }
    }

    private void exportDiff(HashMap<String, String> map, ClassStore model, File outputFile) {
        try (CSVPrinter writer = new CSVPrinter(new FileWriter(outputFile), Stores.getFormat())) {
            Differences.export(map, model, writer);
        } catch (IOException e) {
            logger.error("I/O error with file {}", output, e);
        }
    }

    @Override
    public void printUsage() {
        System.out.println("Usage: metric-history diff <ancestry file> <raw file> <output>");
        System.out.println();
        System.out.println("<ancestry file>     is the path of the file produced by 'ancestry' command");
        System.out.println("<input>             is the path of the file or directory produced by 'convert' command");
        System.out.println("<output>            is the path of the file where the results will be stored or a directory that will contain one file per revision");
    }
}
