package ch.thomsch;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Collection;

/**
 * @author TSC
 */
public class Collector {

    /**
     * Computes the metrics for the element in the folder.
     * @param folder the path to the folder.
     * @return the metrics for this project
     */
    public Metric collect(String folder) {
        final CKReport report = new CK().calculate(folder);

        Metric metric = new Metric();

        for (CKNumber ckNumber : report.all()) {
            metric.add(convertToMetric(ckNumber));
        }

        return metric;
    }

    /**
     * Computes the metrics for the whole folder and then filter the results for the files.
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
            final CKNumber fileMetrics = report.get(FilenameUtils.normalize(file.getAbsolutePath()));
            total.add(convertToMetric(fileMetrics));
        }

        return total;
    }

    private Metric convertToMetric(CKNumber metric) {
        return new Metric(
                metric.getCbo(), metric.getDit(), metric.getNoc(), metric.getNof(),
                metric.getNom(), metric.getRfc(), metric.getWmc(), metric.getLoc());
    }
}
