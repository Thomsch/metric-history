package ch.thomsch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for the application.
 */
public final class Application {
    private final Logger logger = LoggerFactory.getLogger("Application");

    private final Map<String, Command> commands = new HashMap<>();
    private final Command.Help help;

    Application() {
        help = new Command.Help(commands.values());
        addCommand(new Command.Collect());
        addCommand(new Command.Convert());
        addCommand(new Command.Ancestry());
        addCommand(new Command.Difference());
        addCommand(new Command.Mongo());
        addCommand(new Command.Tradeoff());
    }

    /**
     * Parse and execute from the command line.
     *
     * @param args the arguments of the command line.
     *             args[0] contains the action to execute.
     *             The remaining arguments are the parameters for the action.
     */
    void doMain(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }
        final String commandName = args[0];
        final Command command = commands.get(commandName);
        if(command == null) {
            printUnknownCommand(commandName);
            return;
        }

        final String[] parameters = Arrays.copyOfRange(args, 1, args.length);
        if(parameters.length == 0) {
            command.printUsage();
            return;
        }

        if (command.parse(parameters)) {
            try{
                command.execute();
            } catch (Exception e) {
                logger.error("An error occurred: ", e);
            }
        } else {
            System.out.println("Incorrect number of arguments (" + parameters.length + ')');
            System.out.println();
            command.printUsage();
        }
    }

    /**
     * Short form for <code>commands.put(command.getName(), command)</code>.
     * @param command the command
     * @throws IllegalStateException if the command is already present in <code>commands</code>.
     */
    private void addCommand(Command command) {
        if (commands.containsKey(command.getName())) {
            throw new IllegalStateException("The command '" + command.getName() + "' already exists");
        }
        commands.put(command.getName(), command);
    }

    private void printUnknownCommand(String name) {
        System.out.println(
                "Unknown command '" + name + "'. Verify the spelling and make sure your command is in lowercase.");
        System.out.println();
        printHelp();
    }

    private void printHelp() {
        help.execute();
    }

    public static void main(String[] args) {
        new Application().doMain(args);
    }
}
