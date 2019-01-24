package ch.thomsch.cmd;

import ch.thomsch.storage.DiskUtils;

abstract class Command implements Runnable {

    String normalizePath(String arg) {
        return DiskUtils.normalizePath(arg);
    }
}
