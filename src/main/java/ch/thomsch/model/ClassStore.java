package ch.thomsch.model;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.thomsch.metric.Metrics;

/**
 * Represent metrics associated to all classes for multiple revisions.
 * This class is not thread safe.
 *
 * @author Thomsch
 */
public class ClassStore {
    private static final Logger logger = LoggerFactory.getLogger(ClassStore.class);

    private final Map<String, Map<String, Metrics>> data;

    public ClassStore() {
        data = new HashMap<>();
    }

    /**
     * Store a new version of metrics for the given class.
     *
     * @param revision  the version.
     * @param className the canonical name of the class.
     * @param metrics   the metrics associated for the class at the given version.
     */
    public void addMetric(String revision, String className, Metrics metrics) {
        addClassData(revision, className, metrics);
    }

    /**
     * Returns the metric for a class at a revision.
     *
     * @param revision  the revision
     * @param className the class
     * @return the metric or null.
     */
    public Metrics getMetric(String revision, String className) {
        final Map<String, Metrics> metricDump = data.get(revision);
        if (metricDump == null) {
            logger.warn("No such revision {}", revision);
            return null;
        }
        return metricDump.get(className);
    }

    /**
     * Returns the versions.
     */
    public Collection<String> getVersions() {
        return data.keySet();
    }

    /**
     * Returns the classes for a version or null if this revision is unknown.
     * @param revision the revision
     */
    public Collection<String> getClasses(String revision) {
        final Map<String, Metrics> metricMap = data.get(revision);

        if (metricMap == null) {
            return null;
        }

        return metricMap.keySet();
    }

    private void addClassData(String revision, String className, Metrics metrics) {
        Map<String, Metrics> dump = data.computeIfAbsent(revision, key -> new LinkedHashMap<>());

        dump.put(className, metrics);
    }

    public static ClassStore load(CSVParser parser) {
        ClassStore classStore = new ClassStore();

        for (CSVRecord record : parser) {
            classStore.addClassData(record.get(0), record.get(1), convertMetrics(record));
        }

        return classStore;
    }

    private static Metrics convertMetrics(CSVRecord record) {
        Metrics metrics = new Metrics();

        for (int i = 2; i < record.size(); i++) {
            metrics.add(Double.parseDouble(record.get(i)));
        }

        return metrics;
    }

    public static CSVFormat getFormat() {
        return CSVFormat.RFC4180
                .withHeader(getHeader())
                .withDelimiter(';');
    }

    public static String[] getHeader() {
        return new String[]{"revision", "class",
                "LCOM5", "NL", "NLE", "WMC", "CBO", "CBOI", "NII", "NOI", "RFC", "AD",
                "CD", "CLOC", "DLOC", "PDA", "PUA", "TCD", "TCLOC", "DIT", "NOA", "NOC",
                "NOD", "NOP", "LLOC", "LOC", "NA", "NG", "NLA", "NLG", "NLM", "NLPA",
                "NLPM", "NLS", "NM", "NOS", "NPA", "NPM", "NS", "TLLOC", "TLOC", "TNA",
                "TNG", "TNLA", "TNLG", "TNLM", "TNLPA", "TNLPM", "TNLS", "TNM", "TNOS", "TNPA",
                "TNPM", "TNS"};
    }
}
