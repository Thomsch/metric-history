package org.metrichistory;

import org.metrichistory.cmd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 * Entry point for the application.
 */
@CommandLine.Command(name = "metric-history", version = "Metric History 0.6", mixinStandardHelpOptions = true)
public final class Application implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

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
        commandLine.addSubcommand("revision-history", new ReleaseHistory());
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);

        application.setCmd(commandLine);

        try {
            commandLine.parseWithHandler(new CommandLine.RunLast(), args);
        } catch (Exception e) {
            final String message = String.format("An unexpected error occurred: '%s'\nSee the logs (./logs/) for more details.", e.getMessage());
            System.err.println(message);
            logger.error(message, e);
        }
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
