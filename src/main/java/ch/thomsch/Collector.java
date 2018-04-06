package ch.thomsch;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

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
        long loc = 0;
        long nom = 0;
        int count = 0;

        for (CKNumber ckNumber : report.all()) {
            loc += ckNumber.getLoc();
            nom += ckNumber.getNom();
            count++;
        }

        return new Metric((double)loc / count, (double)nom / loc);
    }
}
