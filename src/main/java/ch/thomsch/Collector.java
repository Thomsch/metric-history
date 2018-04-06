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

        long couplingBetweenObjects = 0;
        long depthInheritanceTree = 0;
        long numberOfChildren = 0;
        long numberOfFields = 0;
        long numberOfMethods = 0;
        long responseForAClass = 0;
        long weightMethodClass = 0;
        long lineOfCode = 0;

        for (CKNumber ckNumber : report.all()) {
            couplingBetweenObjects += ckNumber.getCbo();
            depthInheritanceTree += ckNumber.getDit();
            numberOfChildren += ckNumber.getNoc();
            numberOfFields += ckNumber.getNof();
            numberOfMethods += ckNumber.getNom();
            responseForAClass += ckNumber.getRfc();
            weightMethodClass += ckNumber.getWmc();
            lineOfCode += ckNumber.getLoc();
        }

        return new Metric(couplingBetweenObjects, depthInheritanceTree, numberOfChildren, numberOfFields, numberOfMethods, responseForAClass, weightMethodClass, lineOfCode);
    }
}
