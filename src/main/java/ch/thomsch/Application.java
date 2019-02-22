package ch.thomsch;

import ch.thomsch.cmd.Ancestry;
import ch.thomsch.cmd.Collect;
import ch.thomsch.cmd.Convert;
import ch.thomsch.cmd.Difference;
import ch.thomsch.cmd.Filter;
import ch.thomsch.cmd.Mongo;
import ch.thomsch.cmd.Snapshot;
import picocli.CommandLine;

/**
 * Entry point for the application.
 */
@CommandLine.Command(name = "metric-history", version = "Metric History 0.5", mixinStandardHelpOptions = true)
public final class Application implements Runnable {

    private CommandLine commandLine;

    private Application() {
    }

    public static void main(String[] args) {
        final Application application = new Application();

        final CommandLine commandLine = new CommandLine(application);
        commandLine.addSubcommand("ancestry", new Ancestry());
        commandLine.addSubcommand("collect", new Collect());
        commandLine.addSubcommand("convert", new Convert());
        commandLine.addSubcommand("diff", new Difference());
        commandLine.addSubcommand("filter", new Filter());
        commandLine.addSubcommand("mongo", new Mongo());
        commandLine.addSubcommand("snapshot", new Snapshot());

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
