package ch.thomsch.export;

import java.util.ArrayList;
import java.util.List;

import ch.thomsch.metric.Metric;
import ch.thomsch.metric.MetricDump;

/**
 * Default CSV formatter
 *
 * @author TSC
 */
public class DefaultFormatter {

    /**
     * Returns the formatted output for a revision. Each item of the list contains the multiple rows of a CSV line.
     *
     * @param revision       the data's revision number
     * @param parentRevision the parent's revision number
     * @param before         the data for the previous revision
     * @param current        the data for the current version of the project
     * @return the formatted output
     */
    public static List<Object[]> format(String revision, String parentRevision, MetricDump before, MetricDump current) {
        final ArrayList<Object[]> result = new ArrayList<>();

        result.addAll(formatRevision(parentRevision, null, before));
        result.addAll(formatRevision(revision, parentRevision, current));

        return result;
    }

    private static List<Object[]> formatRevision(String revision, String parentRevision, MetricDump dump) {
        final ArrayList<Object[]> result = new ArrayList<>();
        dump.getClasses().forEach(className -> result.add(formatClass(className, revision, parentRevision, dump)));
        return result;
    }

    private static Object[] formatClass(String className, String revision, String parentRevision, MetricDump dump) {
        final Metric metric = dump.getMetric(className);

        final Object[] result = new Object[11];

        result[0] = revision;
        result[1] = parentRevision;
        result[2] = className;
        result[3] = metric.getLineOfCode();
        result[4] = metric.getCouplingBetweenObjects();
        result[5] = metric.getDepthInheritanceTree();
        result[6] = metric.getNumberOfChildren();
        result[7] = metric.getNumberOfFields();
        result[8] = metric.getNumberOfMethods();
        result[9] = metric.getResponseForAClass();
        result[10] = metric.getWeightMethodClass();

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
