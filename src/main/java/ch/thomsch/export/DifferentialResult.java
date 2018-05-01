package ch.thomsch.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import ch.thomsch.metric.Metric;

/**
 * Represents the results for a revision.
 *
 * @author TSC
 */
public class DifferentialResult {

    private final String revision;
    private final Metric metric;

    private DifferentialResult(String revision, Metric metric) {
        this.revision = revision;
        this.metric = metric;
    }

    /**
     * Builds a differential result for a revision.
     *
     * @param revision The revision's SHA
     * @param before   the metrics before the revision
     * @param after    the metrics after applying the revision
     * @return a new instance of results
     */
    public static DifferentialResult build(String revision, Metric before, Metric after) {
        final double cbo = after.getCouplingBetweenObjects() - before.getCouplingBetweenObjects();
        final double dit = after.getDepthInheritanceTree() - before.getDepthInheritanceTree();
        final double noc = after.getNumberOfChildren() - before.getNumberOfChildren();
        final double nof = after.getNumberOfFields() - before.getNumberOfFields();
        final double nom = after.getNumberOfMethods() - before.getNumberOfMethods();
        final double rfc = after.getResponseForAClass() - before.getResponseForAClass();
        final double wmc = after.getWeightMethodClass() - before.getWeightMethodClass();
        final double loc = after.getLineOfCode() - before.getLineOfCode();
        final Metric difference = new Metric(cbo, dit, noc, nof, nom, rfc, wmc, loc);
        return new DifferentialResult(revision, difference);
    }

    /**
     * Formats the revision's SHA and metrics to be printed according to
     * the header's format defined in {@link Reporter}.
     *
     * @return an iterable containing all the results
     */
    public Iterable<?> format() {
        final ArrayList<Object> result = new ArrayList<>();
        result.add(revision);
        result.addAll(exportMetric(metric));

        return result;
    }

    private Collection<Object> exportMetric(Metric metric) {
        return Arrays.asList(
                metric.getLineOfCode(), metric.getCouplingBetweenObjects(), metric.getDepthInheritanceTree(),
                metric.getNumberOfChildren(), metric.getNumberOfFields(), metric.getNumberOfMethods(),
                metric.getResponseForAClass(), metric.getWeightMethodClass());
    }
}
