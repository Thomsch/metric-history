package ch.thomsch.metric;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.thomsch.filter.FileFilter;


/**
 * Collects metrics using the CKMetrics library.
 * https://github.com/mauricioaniche/ck
 * @author Thomsch
 */
public class CKMetrics implements Collector {

    private static final Logger logger = LoggerFactory.getLogger(CKMetrics.class);

    @Override
    public MetricDump collect(String folder, String revision, FileFilter filter) {
        final CKReport report = new CK().calculate(folder);

        final MetricDump dump = new MetricDump();

        report.all().stream()
                .filter(ckNumber -> filter.accept(FilenameUtils.normalize(ckNumber.getFile())))
                .forEach(ckNumber -> dump.add(ckNumber.getClassName(), convertToMetric(ckNumber)));
        return dump;
    }

    private CkMetric convertToMetric(CKNumber metric) {
        return new CkMetric(
                metric.getCbo(), metric.getDit(), metric.getNoc(), metric.getNof(),
                metric.getNom(), metric.getRfc(), metric.getWmc(), metric.getLoc());
    }
}
