package org.metrichistory.cmd;

import org.metrichistory.storage.DiskUtils;

abstract class Command implements Runnable {

    String normalizePath(String arg) {
        return DiskUtils.normalizePath(arg);
    }
}
