package org.metrichistory.model.vcs;

/**
 * Represents a pseudo tag for the start of the project
 */
public class NullTag extends Tag {

    public NullTag() {
        super("", null, "null", 0);
    }

    @Override
    public Tag getPreviousTag() {
        return this;
    }

    @Override
    public boolean isNull(){
        return true;
    }

}
