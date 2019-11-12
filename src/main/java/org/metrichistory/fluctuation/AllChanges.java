package org.metrichistory.fluctuation;

import org.metrichistory.model.Metrics;

import java.util.List;
import java.util.function.BiFunction;

public class AllChanges implements Computer {

    private final BiFunction<Double, Double, Double> calculateDifference;

    public AllChanges(BiFunction<Double, Double, Double> calculateDifference) {
        this.calculateDifference = calculateDifference;
    }

    @Override
    public Metrics compute(Metrics reference, Metrics other) {
        if(other == null && reference == null) return null;

        if(other == null) {
            return reference.copy();
        }

        final List<Double> references;
        if(reference == null) {
            references = new Metrics(other.size()).get();
        } else {
            references = reference.get();
        }

        final List<Double> others = other.get();

        if (references.size() != others.size()) {
            throw new IllegalArgumentException("These metrics are not from the same source!");
        }

        final Metrics result = new Metrics();
        for (int i = 0; i < references.size(); i++) {
            result.add(calculateDifference.apply(others.get(i), references.get(i)));
        }

        return result;
    }
}
