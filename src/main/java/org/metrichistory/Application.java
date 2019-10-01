package org.metrichistory;

import org.metrichistory.cmd.Ancestry;
import org.metrichistory.cmd.Collect;
import org.metrichistory.cmd.Convert;
import org.metrichistory.cmd.Difference;
import org.metrichistory.cmd.FilterRefactoring;
import org.metrichistory.cmd.Mongo;
import org.metrichistory.cmd.RevisionHistory;

import picocli.CommandLine;

/**
 * Entry point for the application.
 */
@CommandLine.Command(name = "metric-history", version = "Metric History 0.6", mixinStandardHelpOptions = true)
public final class Application implements Runnable {

    private CommandLine commandLine;

    private Application() {
    }

    public static void main(String[] args) {
        final Application application = new Application();

        final CommandLine commandLine = new CommandLine(application);
        commandLine.addSubcommand("collect", new Collect());
        commandLine.addSubcommand("diff", new Difference());
        commandLine.addSubcommand("ancestry", new Ancestry());
        commandLine.addSubcommand("convert", new Convert());
        commandLine.addSubcommand("filter", new FilterRefactoring());
        commandLine.addSubcommand("mongo", new Mongo());
        commandLine.addSubcommand("revision-history", new RevisionHistory());
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);

        application.setCmd(commandLine);
        commandLine.parseWithHandler(new CommandLine.RunLast(), args);
    }

    private void setCmd(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public void run() {
        if(commandLine != null) {
            commandLine.usage(System.out);
        }
    }
}
