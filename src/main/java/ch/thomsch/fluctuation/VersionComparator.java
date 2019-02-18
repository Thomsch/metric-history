package ch.thomsch.fluctuation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;

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
    public ClassStore fluctuations(String version, String other, ClassStore data) {
        final Computer computer = new StrictChange();
        final ClassStore results = new ClassStore();

        final Collection<String> artifacts = data.getClasses(version);
        if(artifacts == null) return results;

        for (String artifact : artifacts) {
            final Metrics referenceMeasures = data.getMetric(version, artifact);
            if (referenceMeasures == null) {
                logger.warn("No data for revision {}", version);
                continue;
            }

            final Metrics otherMeasures = data.getMetric(other, artifact);

            final Metrics result = computer.compute(otherMeasures, referenceMeasures);

            results.addMetric(version, artifact, result);
        }
        return results;
    }
}
