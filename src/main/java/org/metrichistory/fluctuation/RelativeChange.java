package org.metrichistory.fluctuation;

import java.util.function.BiFunction;

public class RelativeChange implements BiFunction<Double, Double, Double> {

    /**
     * Computes the change from d1 to d2.
     */
    @Override
    public Double apply(Double d1, Double d2) {
        if(d1 < 0 || d2 < 0) {
            throw new IllegalArgumentException("Negative numbers are not supported");
        }

        if(d1 == 0) {
            d1 += 1;
            d2 += 1;
        }

        return (d2 - d1) / Math.abs(d1);
    }
}
