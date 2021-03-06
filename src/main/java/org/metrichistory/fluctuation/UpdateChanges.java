package org.metrichistory.fluctuation;

import org.metrichistory.model.Metrics;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Specifies a computation between two suites of metrics where we only return a value if both metrics are presents.
 * This put an emphasis on the changes between two version rather than the creation (old == null, current != null) or
 * the destruction (old != null, current == null).
 */
public class UpdateChanges implements ChangesComparator {

    private final BiFunction<Double, Double, Double> calculateDifference;

    public UpdateChanges(BiFunction<Double, Double, Double> calculateDifference) {
        this.calculateDifference = calculateDifference;
    }

    @Override
    public Metrics compute(Metrics reference, Metrics other) {
        Metrics result = null;

        if (other != null && reference != null) {
            final List<Double> others = other.get();
            final List<Double> references = reference.get();

            if (others.size() != references.size()) {
                throw new IllegalArgumentException("These metrics are not from the same source!");
            }

            result = new Metrics();
            for (int i = 0; i < others.size(); i++) {
                result.add(calculateDifference.apply(others.get(i), references.get(i)));
            }
        }

        return result;
    }
}
