package org.metrichistory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds in memory measures for different artifacts in multiple revisions.
 * This class is not thread safe.
 */
public class MeasureStore {
    private final Map<String, Map<String, Metrics>> data;

    public MeasureStore() {
        data = new HashMap<>();
    }

    /**
     * Store a new version of measures for the given class.
     *
     * @param version  the version.
     * @param artifact the identifier of the artifact.
     * @param measure   the measures associated for the artifact at the given version.
     */
    public void add(String version, String artifact, Metrics measure) {
        if(measure == null || artifact == null) return;
        final Map<String, Metrics> dump = data.computeIfAbsent(version, key -> new LinkedHashMap<>());

        dump.put(artifact.intern(), measure);
    }

    /**
     * Returns the metric for a class at a version.
     *
     * @param version  the version
     * @param artifact the class
     * @return the metric or null if it doesn't exists.
     */
    public Metrics get(String version, String artifact) {
        final Map<String, Metrics> metricDump = data.get(version);
        if (metricDump == null) {
            return null;
        }
        return metricDump.get(artifact);
    }

    /**
     * Returns the list of versions currently available in the store.
     */
    public Collection<String> versions() {
        return data.keySet();
    }

    /**
     * Returns all the artifacts for a version or null if this version is unknown.
     * @param version the version
     */
    public Collection<String> artifacts(String version) {
        final Map<String, Metrics> measures = data.get(version);

        if (measures == null) {
            return null;
        }

        return measures.keySet();
    }

    /**
     * Returns the sum of measures instances across all artifacts and versions.
     */
    public long instances() {
        long instances = 0;

        for (String version : versions()) {
            instances += artifacts(version).size();
        }

        return instances;
    }

    /**
     * Returns the number of revisions stored.
     */
    public long versionCount() {
        return versions().size();
    }

    public boolean hasSingleVersion() {
        return versions().size() == 1;
    }

    public boolean hasNoVersion() {
        return versions().size() == 0;
    }
}
