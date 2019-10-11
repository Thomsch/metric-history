package org.metrichistory.storage;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public final class DiskUtils {

    private DiskUtils() {
    }

    public static String normalizePath(String arg) {
        return FilenameUtils.normalize(new File(arg).getAbsolutePath());
    }

}
