package ch.thomsch;

import ch.thomsch.metric.Metric;
import ch.thomsch.model.Raw;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Thomsch
 */
public class DifferenceTest {

    @Test
    public void computesShouldMakeDifference() {
        Difference difference = new Difference();

        Metric a = new Metric(1.0);
        Metric b = new Metric(1.0);
        Metric c = new Metric(2.0);

        Metric adiffb = difference.computes(a, b);
        Metric bdiffc = difference.computes(b, c);
        Metric cdiffb = difference.computes(c, b);

        assertArrayEquals(new Double[]{0.0}, adiffb.get().toArray());
        assertArrayEquals(new Double[]{1.0}, bdiffc.get().toArray());
        assertArrayEquals(new Double[]{-1.0}, cdiffb.get().toArray());
    }

    @Test
    public void computesShouldRespectOrder() {
        Difference difference = new Difference();

        Metric a = new Metric(1.0, 2.0, 3.0);
        Metric b = new Metric(10.0, 20.0, 30.0);

        final Metric actual = difference.computes(a, b);

        final Double[] expected = {9.0, 18.0, 27.0};
        assertArrayEquals(expected, actual.get().toArray());
    }

    @Test
    public void export() throws IOException {
        Difference difference = new Difference();
        HashMap<String, String> ancestry = setupAncestry();

        Raw model = setupModel();

        final StringWriter out = new StringWriter();
        CSVPrinter writer = new CSVPrinter(out, CSVFormat.DEFAULT);
        difference.export(ancestry, model, writer);

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

    private Raw setupModel() {
        Raw raw = new Raw();

        raw.addMetric("a", "X", new Metric(0.0, 1.0, 10.0));
        raw.addMetric("a", "Y", new Metric(0.1, 0.5, 10.0));
        raw.addMetric("a", "W", new Metric(Double.MIN_NORMAL, Double.MIN_NORMAL, Double.MIN_NORMAL));
        raw.addMetric("b", "X", new Metric(0.1, 0.5, 10.0));
        raw.addMetric("b", "Y", new Metric(0.0, 0.5, 5.0));
        raw.addMetric("b", "Z", new Metric(0.0, 0.5, 5.0));
        raw.addMetric("c", "X", new Metric(Double.NaN, Double.NaN, Double.NaN));
        raw.addMetric("d", "X", new Metric(6.0, 5.0, 1.0));
        raw.addMetric("e", "X", new Metric(5.0, 10.0, -20.0));
        raw.addMetric("f", "X", new Metric(5.0, 10.0, -20.0));

        return raw;
    }

    private HashMap<String, String> setupAncestry() {
        HashMap<String, String> ancestry = new LinkedHashMap<>();
        ancestry.put("a", "b");
        ancestry.put("d", "e");
        ancestry.put("e", "f");
        return ancestry;
    }
}
