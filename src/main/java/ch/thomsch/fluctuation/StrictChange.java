package ch.thomsch.fluctuation;

import java.util.List;

import ch.thomsch.model.Metrics;

/**
 * Specifies a computation between two suites of metrics where we only return a value if both metrics are presents.
 * This put an emphasis on the changes between two version rather than the creation (old == null, current != null) or
 * the destruction (old != null, current == null).
 */
public class StrictChange implements Computer {
    @Override
    public Metrics compute(Metrics old, Metrics current) {
        Metrics result = null;

        if (old != null && current != null) {
            final List<Double> as = old.get();
            final List<Double> bs = current.get();

            if (as.size() != bs.size()) {
                throw new IllegalArgumentException("These metrics are not from the same source!");
            }

            result = new Metrics();
            for (int i = 0; i < as.size(); i++) {
                result.add(bs.get(i) - as.get(i));
            }
        }

        return result;
    }
}
