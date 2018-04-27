package ch.thomsch.metric;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

import ch.thomsch.Metric;

/**
 * Collects metrics using the CKMetrics library.
 * https://github.com/mauricioaniche/ck
 * @author TSC
 */
public class CKMetrics implements Collector {

    private static final Logger logger = LoggerFactory.getLogger(CKMetrics.class);

    @Override
    public Metric collect(String folder) {
        final CKReport report = new CK().calculate(folder);
        final Metric metric = new Metric();

        for (CKNumber ckNumber : report.all()) {
            metric.add(convertToMetric(ckNumber));
        }

        return metric;
    }

    @Override
    public Metric collect(String folder, Collection<File> files, String revision) {
        final CKReport rawReport = new CK().calculate(folder);
        final Metric total = new Metric();

        final CKReport report = new CKReport();
        rawReport.all().forEach(ckNumber -> {
            ckNumber.setFile(FilenameUtils.normalize(ckNumber.getFile()));
            report.add(ckNumber);
        });

        for (File file : files) {
            if (isJavaFile(file) && !isTestFile(file)) {
                final String fileName = FilenameUtils.normalize(file.getAbsolutePath());

                final CKNumber fileMetrics = report.get(fileName);
                if (fileMetrics == null) {
                    logger.warn("Could not retrieve metrics for {} at revision {}", fileName, revision);
                } else {
                    total.add(convertToMetric(fileMetrics));
                }
            }
        }
        return total;
    }

    private boolean isTestFile(File file) {
        return FilenameUtils.getBaseName(file.getName()).endsWith("Test") || FilenameUtils.getBaseName(file.getName()
        ).endsWith("Tests");
    }

    private boolean isJavaFile(File file) {
        return FilenameUtils.getExtension(file.getName()).equals("java");
    }

    private Metric convertToMetric(CKNumber metric) {
        return new Metric(
                metric.getCbo(), metric.getDit(), metric.getNoc(), metric.getNof(),
                metric.getNom(), metric.getRfc(), metric.getWmc(), metric.getLoc());
    }
}
