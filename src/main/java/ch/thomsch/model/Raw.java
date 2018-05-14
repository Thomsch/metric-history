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

import ch.thomsch.metric.Metric;

/**
 * Represent metrics associated to a class for multiple revisions.
 * This class is not thread safe.
 *
 * @author Thomsch
 */
public class Raw {
    private static final Logger logger = LoggerFactory.getLogger(Raw.class);

    private final Map<String, Map<String, Metric>> data;

    public Raw() {
        data = new HashMap<>();
    }

    public static Raw load(CSVParser parser) {
        Raw raw = new Raw();

        for (CSVRecord record : parser) {
            raw.addClassData(record.get(0), record.get(1), convertMetrics(record));
        }

        return raw;
    }

    private static Metric convertMetrics(CSVRecord record) {
        Metric metric = new Metric();

        for (int i = 2; i < record.size(); i++) {
            metric.add(Double.parseDouble(record.get(i)));
        }

        return metric;
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

    public void addMetric(String revision, String className, Metric metric) {
        addClassData(revision, className, metric);
    }

    private void addClassData(String revision, String className, Metric metric) {
        Map<String, Metric> dump = data.computeIfAbsent(revision, key -> new LinkedHashMap<>());

        dump.put(className, metric);
    }

    /**
     * Returns the metric for a class at a revision.
     *
     * @param revision  the revision
     * @param className the class
     * @return the metric or null.
     */
    public Metric getMetric(String revision, String className) {
        final Map<String, Metric> metricDump = data.get(revision);
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
     * Returns the classes for a version.
     *
     * @param revision the revision
     */
    public Collection<String> getClasses(String revision) {
        return data.get(revision).keySet();
    }
}
