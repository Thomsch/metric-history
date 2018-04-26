package ch.thomsch;

/**
 * Metrics collected for a project.
 * @author TSC
 */
public class Metric {
    private double couplingBetweenObjects;
    private double depthInheritanceTree;
    private double numberOfChildren;
    private double numberOfFields;
    private double numberOfMethods;
    private double responseForAClass;
    private double weightMethodClass;
    private double lineOfCode;

    public Metric(
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

    public Metric() {
        this(0, 0, 0, 0, 0, 0, 0, 0);
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

    public void add(Metric metric) {
        couplingBetweenObjects += metric.couplingBetweenObjects;
        depthInheritanceTree += metric.depthInheritanceTree;
        numberOfChildren += metric.numberOfChildren;
        numberOfFields += metric.numberOfFields;
        numberOfMethods += metric.numberOfMethods;
        responseForAClass += metric.responseForAClass;
        weightMethodClass += metric.weightMethodClass;
        lineOfCode += metric.lineOfCode;
    }
}
