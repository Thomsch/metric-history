package org.metrichistory.versioncontrol;

public class VcsOperationException extends Exception {
    public VcsOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
