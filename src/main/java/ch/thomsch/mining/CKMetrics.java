package ch.thomsch.mining;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

import ch.thomsch.model.MetricDump;
import ch.thomsch.model.Metrics;


/**
 * Collects metrics using the CKMetrics library.
 * https://github.com/mauricioaniche/ck
 */
public class CKMetrics implements Analyzer {

    private final Map<String, MetricDump> results;

    public CKMetrics() {
        results = new HashMap<>();
    }

    @Override
    public void execute(String revision, String folder, FileFilter filter) {
        final CKReport report = new CK().calculate(folder);

        final MetricDump dump = new MetricDump();

        report.all().stream()
                .filter(ckNumber -> filter.accept(FilenameUtils.normalize(ckNumber.getFile())))
                .forEach(ckNumber -> dump.add(ckNumber.getClassName(), convertToMetric(ckNumber)));

        results.put(revision, dump);
    }

    @Override
    public void postExecute(String version) {
        // Nothing needs to be done
    }

    @Override
    public boolean hasInCache(String version) {
        return results.get(version) != null;
    }

    public MetricDump getResult(String version) {
        return results.get(version);
    }

    private Metrics convertToMetric(CKNumber metric) {
        return new Metrics(
                (double) metric.getCbo(), (double) metric.getDit(), (double) metric.getNoc(), (double) metric.getNof(),
                (double) metric.getNom(), (double) metric.getRfc(), (double) metric.getWmc(), (double) metric.getLoc());
    }
}
