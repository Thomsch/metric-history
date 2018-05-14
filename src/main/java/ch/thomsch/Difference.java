package ch.thomsch;

import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.thomsch.metric.Metric;
import ch.thomsch.model.Raw;

/**
 * @author Thomsch
 */
public class Difference {
    private static final Logger logger = LoggerFactory.getLogger(Difference.class);

    /**
     * Computes and export the differences in metric for classes between a revision and its parent for all revisions
     * present in <code>ancestry</code>.
     * If a class is added or deleted in between, this class is ignored.
     *
     * @param ancestry the list of revisions with the relation to their parent
     * @param model    contains the metrics in relation with their revisions and classes
     * @param writer   where the results are written
     */
    public void export(Ancestry ancestry, Raw model, CSVPrinter writer) {
        for (Map.Entry<String, String> revisionParent : ancestry.getRevisions()) {

            String revision = revisionParent.getKey();
            String parent = revisionParent.getValue();

            for (String className : model.getClasses(revision)) {
                Metric revisionMetric = model.getMetric(revision, className);
                Metric parentMetric = model.getMetric(parent, className);

                if (revisionMetric != null && parentMetric != null) {
                    Metric result = computes(parentMetric, revisionMetric);
                    try {
                        writer.printRecord(format(revision, className, result));
                    } catch (IOException e) {
                        logger.error("Cannot write result for class " + className + " for revision " + revision);
                    }
                }
            }
        }
    }

    private Object[] format(String revision, String className, Metric metrics) {
        ArrayList<Object> result = new ArrayList<>();
        result.add(revision);
        result.add(className);
        result.addAll(metrics.get());

        return result.toArray();
    }

    /**
     * Computes the difference between a and b (b - a) for each of their metrics.
     * It uses the order of the metrics given by {@link Metric#get()}.
     *
     * @param a the left operand
     * @param b the right operand
     * @return <code>b</code> - <code>a</code>
     * @throws IllegalArgumentException if the metrics are not comparable
     * @throws NullPointerException     if a or b are null
     */
    public Metric computes(Metric a, Metric b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        List<Double> as = a.get();
        List<Double> bs = b.get();

        if (as.size() != bs.size()) {
            throw new IllegalArgumentException("These metrics are not from the same source!");
        }

        Metric result = new Metric();
        for (int i = 0; i < as.size(); i++) {
            result.add(bs.get(i) - as.get(i));
        }
        return result;
    }
}
