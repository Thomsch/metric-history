package org.metrichistory.storage;

import java.util.List;

public interface RevisionSource {
    List<String> getVersions();
}
