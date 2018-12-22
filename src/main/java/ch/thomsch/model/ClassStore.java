package ch.thomsch.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represent metrics associated to all classes for multiple revisions.
 * This class is not thread safe.
 *
 * @author Thomsch
 */
public class ClassStore {
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
        if(metrics == null || className == null) return;
        final Map<String, Metrics> dump = data.computeIfAbsent(revision, key -> new LinkedHashMap<>());

        dump.put(className.intern(), metrics);
    }

    /**
     * Returns the metric for a class at a revision.
     *
     * @param revision  the revision
     * @param className the class
     * @return the metric or null if it doesn't exists.
     */
    public Metrics getMetric(String revision, String className) {
        final Map<String, Metrics> metricDump = data.get(revision);
        if (metricDump == null) {
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

    /**
     * Returns the number of class versions stored in this object.
     */
    public long instances() {
        long instances = 0;

        for (String version : getVersions()) {
            instances += getClasses(version).size();
        }

        return instances;
    }

    /**
     * Returns the number of revisions stored.
     */
    public long revisions() {
        return getVersions().size();
    }
}
