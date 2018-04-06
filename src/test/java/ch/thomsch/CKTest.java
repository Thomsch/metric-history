package ch.thomsch;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKNumber;
import com.github.mauricioaniche.ck.CKReport;

import org.junit.Test;

/**
 * @author TSC
 */
public class CKTest {
    @Test
    public void calculateMetricForProject() {
        final CKReport report = new CK().calculate(".");
        for (CKNumber ckNumber : report.all()) {
            System.out.println(ckNumber);
        }
    }
}
