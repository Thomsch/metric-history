package ch.thomsch.metric;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

import org.apache.commons.io.FilenameUtils;
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
    public Metric collect(String folder, String revision) {
        final CKReport rawReport = new CK().calculate(folder);
        final Metric total = new Metric();

        final CKReport report = new CKReport();
        rawReport.all().forEach(ckNumber -> {
            ckNumber.setFile(FilenameUtils.normalize(ckNumber.getFile()));
            report.add(ckNumber);
        });

        report.all().stream()
                .filter(ckNumber -> !isTestClass(ckNumber.getClassName()))
                .forEachOrdered(ckNumber -> total.add(convertToMetric(ckNumber)));

        return total;
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
