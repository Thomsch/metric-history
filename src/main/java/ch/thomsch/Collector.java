package ch.thomsch;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * @author TSC
 */
public class Collector {

    private static final Logger logger = LoggerFactory.getLogger(Collector.class);

    /**
     * Computes the metrics for the element in the folder.
     * @param folder the path to the folder.
     * @return the metrics for this project
     */
    public Metric collect(String folder) {
        final CKReport report = new CK().calculate(folder);
        final Metric metric = new Metric();

        for (CKNumber ckNumber : report.all()) {
            metric.add(convertToMetric(ckNumber));
        }

        return metric;
    }

    /**
     * Computes the metrics for the whole folder and then filter the results for the files.
     * Only java files that do not end with "Test(s)" are considered.
     *
     * @param folder the path to the folder.
     * @param files  the files to which the results are filtered
     * @return the metrics for this project
     */
    public Metric collect(String folder, Collection<File> files) {
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
                    logger.warn("Could not retrieve metrics for {}", fileName);
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
