package ch.thomsch.cmd;

import ch.thomsch.storage.DiskUtils;

public abstract class Command {

    /**
     * Returns the name of the command. This is also the string to use to call it in command line.
     * @return the name, in lowercase.
     */
    public abstract String getName();

    /**
     * Parses the parameters for the command.
     * @param parameters the parameters to parse
     * @return <code>true</code> if the parameters could be parsed, <code>false</code> otherwise.
     */
    public abstract boolean parse(String[] parameters);

    /**
     * Executes the command.
     */
    public abstract void execute() throws Exception;

    /**
     * Prints the usage of the command on <code>System.out</code>.
     */
    public abstract void printUsage();

    String normalizePath(String arg) {
        return DiskUtils.normalizePath(arg);
    }
}
