package ch.thomsch.storage;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;

/**
 * Loads CSV files
 */
public final class Stores {
    private static final Logger logger = LoggerFactory.getLogger(Stores.class);

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
     * @param model the recipient to load data to.
     * @return the same instance of <code>model</code>.
     * @throws FileNotFoundException when the file cannot be found.
     * @throws IOException           when there is a reading problem with the disk.
     * @throws NullPointerException when no <code>model</code> is given
     */
    public static ClassStore loadClasses(String filePath, ClassStore model) throws IOException {
        Objects.requireNonNull(model);
        logger.info("Loading raw (" + filePath + ")...");

        final CSVParser parser = new CSVParser(new FileReader(filePath), getFormat().withSkipHeaderRecord());

        for (CSVRecord record : parser) {
            model.addMetric(record.get(0), record.get(1), convertMetrics(record));
        }

        return model;
    }

    /**
     * Loads a {@link ClassStore} in CSV format from the disk.
     *
     * @param filePath the path of the CSV file
     * @return a new instance of {@link ClassStore}
     * @throws FileNotFoundException when the file cannot be found.
     * @throws IOException           when there is a reading problem with the disk.
     */
    public static ClassStore loadClasses(String filePath) throws IOException {
        return loadClasses(filePath, new ClassStore());
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

        final String[] labels = getLabels();
        final HashMap<String, Double> map = new HashMap<>();
        for (int i = 0; i < labels.length; i++) {
            final String label = labels[i].toLowerCase();
            map.put(label, metrics.get(i));
        }
        return map;
    }

    private static String[] getLabels() {
        return Arrays.copyOfRange(HEADER_SOURCEMETER, 2, HEADER_SOURCEMETER.length);
    }

    public static int[] getIndices(String ... metricsLabels) {
        final int[] indices = new int[metricsLabels.length];

        for (int i = 0; i < metricsLabels.length; i++) {
            indices[i] = Arrays.asList(getLabels()).indexOf(metricsLabels[i]);
        }
        return indices;
    }
}
