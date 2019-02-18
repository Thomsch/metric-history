package ch.thomsch.fluctuation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;

public final class Differences {
    private static final Logger logger = LoggerFactory.getLogger(Differences.class);

    private Differences() {
    }

    public static ClassStore calculate(String version, String parent, ClassStore data) {
        final Computer computer = new StrictChange();
        final ClassStore results = new ClassStore();

        final Collection<String> artifacts = data.getClasses(version);
        if(artifacts == null) return results;

        for (String artifact : artifacts) {
            final Metrics current = data.getMetric(version, artifact);
            if (current == null) {
                logger.warn("No data for revision {}", version);
                continue;
            }

            final Metrics previous = data.getMetric(parent, artifact);

            final Metrics result = computer.compute(previous, current);

            results.addMetric(version, artifact, result);
        }
        return results;
    }
}
