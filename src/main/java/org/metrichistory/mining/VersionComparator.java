package org.metrichistory.mining;

import org.metrichistory.fluctuation.Computer;
import org.metrichistory.fluctuation.UpdateChanges;
import org.metrichistory.model.MeasureStore;
import org.metrichistory.model.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Compare the artifacts of two version of a project.
 */
public final class VersionComparator {
    private static final Logger logger = LoggerFactory.getLogger(VersionComparator.class);

    /**
     * Computes the fluctuations in metrics of two versions of a project.
     * @param version the reference version
     * @param other the other version
     * @param data contains the measurements for at least the two versions of the project
     * @return a new instance containing the fluctuations for <code>version</code>
     */
    public MeasureStore fluctuations(String version, String other, MeasureStore data) {
        final Computer computer = new UpdateChanges();
        final MeasureStore results = new MeasureStore();

        final Collection<String> artifacts = data.artifacts(version);
        if(artifacts == null) return results;

        for (String artifact : artifacts) {
            final Metrics referenceMeasures = data.get(version, artifact);
            if (referenceMeasures == null) {
                logger.warn("No data for revision {}", version);
                continue;
            }

            final Metrics otherMeasures = data.get(other, artifact);

            final Metrics result = computer.compute(referenceMeasures, otherMeasures);

            results.add(version, artifact, result);
        }
        return results;
    }
}
