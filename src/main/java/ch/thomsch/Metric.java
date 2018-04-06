package ch.thomsch;

/**
 * Metrics collected for a project.
 * @author TSC
 */
public class Metric {
    private final double couplingBetweenObjects;
    private final double depthInheritanceTree;
    private final double numberOfChildren;
    private final double numberOfFields;
    private final double numberOfMethods;
    private final double responseForAClass;
    private final double weightMethodClass;
    private final double lineOfCode;

    Metric(
            double couplingBetweenObjects,
            double depthInheritanceTree,
            double numberOfChildren,
            double numberOfFields,
            double numberOfMethods,
            double responseForAClass,
            double weightMethodClass,
            double lineOfCode) {
        this.couplingBetweenObjects = couplingBetweenObjects;
        this.depthInheritanceTree = depthInheritanceTree;
        this.numberOfChildren = numberOfChildren;
        this.numberOfFields = numberOfFields;
        this.numberOfMethods = numberOfMethods;
        this.responseForAClass = responseForAClass;
        this.weightMethodClass = weightMethodClass;
        this.lineOfCode = lineOfCode;
    }

    public double getCouplingBetweenObjects() {
        return couplingBetweenObjects;
    }

    public double getDepthInheritanceTree() {
        return depthInheritanceTree;
    }

    public double getNumberOfChildren() {
        return numberOfChildren;
    }

    public double getNumberOfFields() {
        return numberOfFields;
    }

    public double getNumberOfMethods() {
        return numberOfMethods;
    }

    public double getResponseForAClass() {
        return responseForAClass;
    }

    public double getWeightMethodClass() {
        return weightMethodClass;
    }

    public double getLineOfCode() {
        return lineOfCode;
    }
}
