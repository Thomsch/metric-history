package ch.thomsch.mining;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

import org.apache.commons.io.FilenameUtils;

import ch.thomsch.model.MetricDump;
import ch.thomsch.model.Metrics;


/**
 * Collects metrics using the CKMetrics library.
 * https://github.com/mauricioaniche/ck
 * @author Thomsch
 */
public class CKMetrics implements Analyzer {

    @Override
    public void execute(String revision, String folder, FileFilter filter) {
        final CKReport report = new CK().calculate(folder);

        final MetricDump dump = new MetricDump();

        report.all().stream()
                .filter(ckNumber -> filter.accept(FilenameUtils.normalize(ckNumber.getFile())))
                .forEach(ckNumber -> dump.add(ckNumber.getClassName(), convertToMetric(ckNumber)));
    }

    @Override
    public void postExecute(String revision) {
        // Nothing needs to be done
    }

    @Override
    public boolean hasInCache(String version) {
        return false;
    }

    private Metrics convertToMetric(CKNumber metric) {
        return new Metrics(
                (double) metric.getCbo(), (double) metric.getDit(), (double) metric.getNoc(), (double) metric.getNof(),
                (double) metric.getNom(), (double) metric.getRfc(), (double) metric.getWmc(), (double) metric.getLoc());
    }
}
