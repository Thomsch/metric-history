package ch.thomsch.storage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ch.thomsch.metric.Metrics;
import ch.thomsch.storage.ClassStore;

/**
 * Loads CSV files
 */
public final class Stores {
    private static final int NUMBER_OF_SOURCEMETER_METRICS = 52;

    private static final String[] HEADER_SOURCEMETER = {"revision", "class", "LCOM5", "NL", "NLE", "WMC", "CBO", "CBOI",
            "NII", "NOI", "RFC", "AD", "CD", "CLOC", "DLOC", "PDA", "PUA", "TCD", "TCLOC", "DIT", "NOA", "NOC", "NOD",
            "NOP", "LLOC", "LOC", "NA", "NG", "NLA", "NLG", "NLM", "NLPA", "NLPM", "NLS", "NM", "NOS", "NPA", "NPM",
            "NS", "TLLOC", "TLOC", "TNA", "TNG", "TNLA", "TNLG", "TNLM", "TNLPA", "TNLPM", "TNLS", "TNM", "TNOS",
            "TNPA", "TNPM", "TNS"};

    private Stores() {
    }

    /**
     * Loads a {@link ClassStore} in CSV format from the disk.
     *
     * @param filePath the path of the CSV file
     * @return a new instance of ClassStore
     * @throws FileNotFoundException if the file cannot be found.
     * @throws IOException           if there is a reading problem with the disk.
     */
    public static ClassStore loadClasses(String filePath) throws IOException {
        final CSVParser parser = new CSVParser(new FileReader(filePath), getFormat().withSkipHeaderRecord());
        return load(parser);
    }

    private static ClassStore load(CSVParser parser) {
        final ClassStore classStore = new ClassStore();

        for (CSVRecord record : parser) {
            classStore.addMetric(record.get(0), record.get(1), convertMetrics(record));
        }

        return classStore;
    }

    private static Metrics convertMetrics(CSVRecord record) {
        final Metrics metrics = new Metrics();

        for (int i = 2; i < record.size(); i++) {
            metrics.add(Double.parseDouble(record.get(i)));
        }

        return metrics;
    }

    public static CSVFormat getFormat() {
        return CSVFormat.RFC4180
                .withHeader(HEADER_SOURCEMETER)
                .withDelimiter(';');
    }

    /**
     * Transforms the list of metric into a labelled list.
     *
     * @return the map
     */
    public static Map<String, Double> convertToSourceMeterFormat(Metrics metrics) {
        if (metrics.size() != NUMBER_OF_SOURCEMETER_METRICS) {
            throw new IllegalStateException("These metrics are not compatible with the SourceMeter's format");
        }

        final String[] labels = Arrays.copyOfRange(HEADER_SOURCEMETER, 2, HEADER_SOURCEMETER.length);
        final HashMap<String, Double> map = new HashMap<>();
        for (int i = 0; i < labels.length; i++) {
            final String label = labels[i].toLowerCase();
            map.put(label, metrics.get(i));
        }
        return map;
    }
}
