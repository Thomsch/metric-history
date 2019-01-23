package ch.thomsch.cmd;

import ch.thomsch.storage.DiskUtils;

public abstract class Command implements Runnable {

    /**
     * Executes the command.
     */
    public abstract void execute() throws Exception;

    String normalizePath(String arg) {
        return DiskUtils.normalizePath(arg);
    }
}
