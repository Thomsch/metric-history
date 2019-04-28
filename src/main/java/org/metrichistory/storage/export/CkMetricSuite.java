package org.metrichistory.storage.export;

import org.metrichistory.model.Metrics;

/**
 *  Utility class that return the correct metric given a name.
 */
final class CkMetricSuite {

    private CkMetricSuite() {
    }

    static double getCouplingBetweenObjects(Metrics metrics) {
        return metrics.get(0);
    }

    static double getDepthInheritanceTree(Metrics metrics) {
        return metrics.get(1);
    }

    static double getNumberOfChildren(Metrics metrics) {
        return metrics.get(2);
    }

    static double getNumberOfFields(Metrics metrics) {
        return metrics.get(3);
    }

    static double getNumberOfMethods(Metrics metrics) {
        return metrics.get(4);
    }

    static double getResponseForAClass(Metrics metrics) {
        return metrics.get(5);
    }

    static double getWeightMethodClass(Metrics metrics) {
        return metrics.get(6);
    }

    static double getLineOfCode(Metrics metrics) {
        return metrics.get(7);
    }
}
