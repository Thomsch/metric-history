package ch.thomsch.storage.export;

import java.util.ArrayList;
import java.util.List;

import ch.thomsch.model.MetricDump;
import ch.thomsch.model.Metrics;

/**
 * Default CSV formatter
 *
 * @author Thomsch
 */
public class DefaultFormatter {

    /**
     * Returns the formatted output for a revision. Each item of the list contains the rows of a CSV line.
     *
     * @param revision       the data's revision number
     * @param parentRevision the parent's revision number
     * @param current        the data for the current version of the project
     * @return the formatted output
     */
    public static List<Object[]> format(String revision, String parentRevision, MetricDump current) {
        final ArrayList<Object[]> result = new ArrayList<>();
        current.getClasses().forEach(className -> result.add(formatClass(className, revision, parentRevision,
                current)));
        return result;
    }

    private static Object[] formatClass(String className, String revision, String parentRevision, MetricDump dump) {
        final Metrics metric = dump.getMetric(className);

        final Object[] result = new Object[11];

        result[0] = revision;
        result[1] = parentRevision;
        result[2] = className;
        result[3] = CkMetricSuite.getLineOfCode(metric);
        result[3] = CkMetricSuite.getLineOfCode(metric);
        result[4] = CkMetricSuite.getCouplingBetweenObjects(metric);
        result[5] = CkMetricSuite.getDepthInheritanceTree(metric);
        result[6] = CkMetricSuite.getNumberOfChildren(metric);
        result[7] = CkMetricSuite.getNumberOfFields(metric);
        result[8] = CkMetricSuite.getNumberOfMethods(metric);
        result[9] = CkMetricSuite.getResponseForAClass(metric);
        result[10] = CkMetricSuite.getWeightMethodClass(metric);

        return result;
    }

    public static Object[] getMetaData() {
        return new Object[]{"revision", "parent revision", "class name", "lines of code", "coupling between objects",
                "depth inheritance tree", "number of children", "number of fields", "number of methods",
                "response for a class", "weight method class"};
    }

    public static Object[] getDifferentialMetaData() {
        return new Object[]{
                "revision", "Lines of code", "Coupling between objects", "Depth inheritance tree", "Number of children",
                "Number of fields", "Number of methods", "Response for a class", "Weight method class"};
    }
}
