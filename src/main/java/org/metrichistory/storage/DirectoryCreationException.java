package org.metrichistory.storage;

import java.io.IOException;

public class DirectoryCreationException extends IOException {
    public DirectoryCreationException(String message) {
        super(message);
    }
}
