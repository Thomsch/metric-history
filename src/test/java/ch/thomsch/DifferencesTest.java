package ch.thomsch;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import ch.thomsch.fluctuation.Differences;
import ch.thomsch.model.ClassStore;
import ch.thomsch.model.Metrics;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Thomsch
 */
public class DifferencesTest {

    @Test
    public void computesShouldMakeDifference() {
        final Metrics a = new Metrics(1.0);
        final Metrics b = new Metrics(1.0);
        final Metrics c = new Metrics(2.0);

        final Metrics adiffb = Differences.computes(a, b);
        final Metrics bdiffc = Differences.computes(b, c);
        final Metrics cdiffb = Differences.computes(c, b);

        assertArrayEquals(new Double[]{0.0}, adiffb.get().toArray());
        assertArrayEquals(new Double[]{1.0}, bdiffc.get().toArray());
        assertArrayEquals(new Double[]{-1.0}, cdiffb.get().toArray());
    }

    @Test
    public void computesShouldRespectOrder() {
        final Metrics a = new Metrics(1.0, 2.0, 3.0);
        final Metrics b = new Metrics(10.0, 20.0, 30.0);

        final Metrics actual = Differences.computes(a, b);

        final Double[] expected = {9.0, 18.0, 27.0};
        assertArrayEquals(expected, actual.get().toArray());
    }

    @Test
    public void export() throws IOException {
        final HashMap<String, String> ancestry = setupAncestry();
        final ClassStore model = setupModel();
        final StringWriter out = new StringWriter();
        final CSVPrinter writer = new CSVPrinter(out, CSVFormat.DEFAULT);

        Differences.export(ancestry, model, writer);

        assertArrayEquals(expectedExport(), out.toString().split("\\r?\\n"));
    }

    private String[] expectedExport() {
        return new String[]{
                "a,X,-0.1,0.5,0.0",
                "a,Y,0.1,0.0,5.0",
                "d,X,1.0,-5.0,21.0",
                "e,X,0.0,0.0,0.0"
        };
    }

    private ClassStore setupModel() {
        final ClassStore classStore = new ClassStore();

        classStore.addMetric("a", "X", new Metrics(0.0, 1.0, 10.0));
        classStore.addMetric("a", "Y", new Metrics(0.1, 0.5, 10.0));
        classStore.addMetric("a", "W", new Metrics(2.0, 3.0, 4.0));
        classStore.addMetric("b", "X", new Metrics(0.1, 0.5, 10.0));
        classStore.addMetric("b", "Y", new Metrics(0.0, 0.5, 5.0));
        classStore.addMetric("b", "Z", new Metrics(0.0, 0.5, 5.0));
        classStore.addMetric("c", "X", new Metrics(Double.NaN, Double.NaN, Double.NaN));
        classStore.addMetric("d", "X", new Metrics(6.0, 5.0, 1.0));
        classStore.addMetric("e", "X", new Metrics(5.0, 10.0, -20.0));
        classStore.addMetric("f", "X", new Metrics(5.0, 10.0, -20.0));

        return classStore;
    }

    private HashMap<String, String> setupAncestry() {
        final HashMap<String, String> ancestry = new LinkedHashMap<>();
        ancestry.put("a", "b");
        ancestry.put("d", "e");
        ancestry.put("e", "f");
        return ancestry;
    }

    @Test
    public void bothMissingOperandsReturnNull() {
        assertNull(Differences.computes(null, null));
    }

    @Test
    public void computesDoNotAllowsMissingFirstOperand() {
        final Metrics m = new Metrics(1.0, 2.0, 3.0);

        final Metrics actual = Differences.computes(null, m);
        assertNull(actual);
    }

    @Test
    public void computesDoNotAllowsMissingSecondOperand() {
        final Metrics m = new Metrics(1.0, 2.0, 3.0);

        final Metrics actual = Differences.computes(m, null);
        assertNull(actual);
    }
}
