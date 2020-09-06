package org.metrichistory.cli;

import org.metrichistory.storage.DiskUtils;

abstract class Command implements Runnable {

    String normalizePath(String arg) {
        if(arg == null)
            return null;
        return DiskUtils.normalizePath(arg);
    }
}
