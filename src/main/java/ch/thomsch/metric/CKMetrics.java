package ch.thomsch.metric;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.thomsch.Metric;

/**
 * Collects metrics using the CKMetrics library.
 * https://github.com/mauricioaniche/ck
 * @author TSC
 */
public class CKMetrics implements Collector {

    private static final Logger logger = LoggerFactory.getLogger(CKMetrics.class);

    @Override
    public MetricDump collect(String folder, String revision) {
        final CKReport report = new CK().calculate(folder);

        final MetricDump dump = new MetricDump();

        report.all().stream()
                .filter(ckNumber -> !isTestClass(ckNumber.getClassName()))
                .forEach(ckNumber -> dump.add(ckNumber.getClassName(), convertToMetric(ckNumber)));
        return dump;
    }

    private boolean isTestClass(String name) {
        name = ClassUtils.getShortClassName(name);
        return name.endsWith("Test") || name.endsWith("Tests");
    }

    private Metric convertToMetric(CKNumber metric) {
        return new Metric(
                metric.getCbo(), metric.getDit(), metric.getNoc(), metric.getNof(),
                metric.getNom(), metric.getRfc(), metric.getWmc(), metric.getLoc());
    }
}
