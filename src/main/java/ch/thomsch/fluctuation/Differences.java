package ch.thomsch.fluctuation;

import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.thomsch.model.Metrics;
import ch.thomsch.model.ClassStore;

/**
 * @author Thomsch
 */
public final class Differences {
    private static final Logger logger = LoggerFactory.getLogger(Differences.class);

    private Differences() {

    }

    /**
     * Computes and export the differences in metric for classes between a revision and its parent for all revisions
     * present in <code>ancestry</code>.
     * If a class is added or deleted in between, this class is ignored.
     *
     * @param ancestry the list of revisions with the relation to their parent
     * @param model    contains the metrics in relation with their revisions and classes
     * @param writer   where the results are written
     */
    public static void export(HashMap<String, String> ancestry, ClassStore model, CSVPrinter writer) {
        final LinkedList<Map.Entry<String, String>> entries = new LinkedList<>(ancestry.entrySet());
        for (Map.Entry<String, String> revisionParent : entries) {

            final String revision = revisionParent.getKey();
            final String parent = revisionParent.getValue();

            logger.info("Exporting revision {} parent is ({})", revision, parent);
            final Collection<String> classes = model.getClasses(revision);
            if (classes == null) {
                logger.warn("No data for revision {}", revision);
                continue;
            }
            for (String className : classes) {
                final Metrics revisionMetrics = model.getMetric(revision, className);
                final Metrics parentMetrics = model.getMetric(parent, className);

                final Metrics result = computes(parentMetrics, revisionMetrics);
                if (result != null) {
                    outputMetric(writer, revision, className, result);
                }
            }
        }
    }

    public static void outputMetric(CSVPrinter writer, String revision, String className, Metrics result) {
        try {
            writer.printRecord(format(revision, className, result));
        } catch (IOException e) {
            logger.error("Cannot write result for class " + className + " for revision " + revision);
        }
    }

    private static Object[] format(String revision, String className, Metrics metrics) {
        final ArrayList<Object> result = new ArrayList<>();
        result.add(revision);
        result.add(className);
        result.addAll(metrics.get());

        return result.toArray();
    }

    /**
     * Computes the difference between a and b (b - a) for each of their metrics.
     * <code>a</code> can be seen as previous and <code>b</code> as current.
     * It uses the order of the metrics given by {@link Metrics#get()}.
     *
     * @param a the left operand
     * @param b the right operand
     * @return <code>b</code> - <code>a</code> or null if a or b is missing.
     * @throws IllegalArgumentException if the metrics are not comparable
     * @throws NullPointerException     if a or b are null
     */
    public static Metrics computes(Metrics a, Metrics b) {
        Metrics result = null;

        if (a != null && b != null) {
            final List<Double> as = a.get();
            final List<Double> bs = b.get();

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
