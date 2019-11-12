package org.metrichistory.fluctuation;

import java.util.function.BiFunction;

public class AbsoluteChange implements BiFunction<Double, Double, Double> {
    @Override
    public Double apply(Double d1, Double d2) {
        return d2 - d1;
    }
}
