package ch.thomsch;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import ch.thomsch.metric.Metrics;
import ch.thomsch.model.Raw;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Thomsch
 */
public class DifferenceTest {

    @Test
    public void computesShouldMakeDifference() {
        Difference difference = new Difference();

        Metrics a = new Metrics(1.0);
        Metrics b = new Metrics(1.0);
        Metrics c = new Metrics(2.0);

        Metrics adiffb = difference.computes(a, b);
        Metrics bdiffc = difference.computes(b, c);
        Metrics cdiffb = difference.computes(c, b);

        assertArrayEquals(new Double[]{0.0}, adiffb.get().toArray());
        assertArrayEquals(new Double[]{1.0}, bdiffc.get().toArray());
        assertArrayEquals(new Double[]{-1.0}, cdiffb.get().toArray());
    }

    @Test
    public void computesShouldRespectOrder() {
        Difference difference = new Difference();

        Metrics a = new Metrics(1.0, 2.0, 3.0);
        Metrics b = new Metrics(10.0, 20.0, 30.0);

        final Metrics actual = difference.computes(a, b);

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

        raw.addMetric("a", "X", new Metrics(0.0, 1.0, 10.0));
        raw.addMetric("a", "Y", new Metrics(0.1, 0.5, 10.0));
        raw.addMetric("a", "W", new Metrics(Double.MIN_NORMAL, Double.MIN_NORMAL, Double.MIN_NORMAL));
        raw.addMetric("b", "X", new Metrics(0.1, 0.5, 10.0));
        raw.addMetric("b", "Y", new Metrics(0.0, 0.5, 5.0));
        raw.addMetric("b", "Z", new Metrics(0.0, 0.5, 5.0));
        raw.addMetric("c", "X", new Metrics(Double.NaN, Double.NaN, Double.NaN));
        raw.addMetric("d", "X", new Metrics(6.0, 5.0, 1.0));
        raw.addMetric("e", "X", new Metrics(5.0, 10.0, -20.0));
        raw.addMetric("f", "X", new Metrics(5.0, 10.0, -20.0));

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
