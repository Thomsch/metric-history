package ch.thomsch.fluctuation;

import java.util.List;

import ch.thomsch.model.Metrics;

public class AllChange implements Computer {
    @Override
    public Metrics compute(Metrics old, Metrics current) {
        if(old == null && current == null) return null;

        if(old == null) {
            return current.copy();
        }

        final List<Double> olds;
        if(current == null) {
            olds = new Metrics(old.size()).get();
        } else {
            olds = current.get();
        }

        final List<Double> currents = old.get();

        if (currents.size() != olds.size()) {
            throw new IllegalArgumentException("These metrics are not from the same source!");
        }

        final Metrics result = new Metrics();
        for (int i = 0; i < currents.size(); i++) {
            result.add(olds.get(i) - currents.get(i));
        }

        return result;
    }
}
