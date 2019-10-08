package org.metrichistory.analyzer.ck;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;
import org.apache.commons.io.FilenameUtils;
import org.metrichistory.analyzer.Analyzer;
import org.metrichistory.mining.FileFilter;
import org.metrichistory.model.MetricDump;
import org.metrichistory.model.Metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


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

    @Override
    public Optional<String> getOutputPath(String version) {
        return Optional.empty();
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
