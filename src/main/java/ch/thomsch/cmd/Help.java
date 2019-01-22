package ch.thomsch.cmd;

import java.util.Collection;
import java.util.Objects;

public class Help extends Command {
    private final Collection<Command> values;

    public Help(Collection<Command> values) {
        this.values = Objects.requireNonNull(values);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public boolean parse(String[] parameters) {
        return true; // No parameters for this command
    }

    @Override
    public void execute() {
        System.out.println("Usage: metric-history <command> <parameters>...");
        System.out.println("or metric-history <command> to learn more about a particular command.");
        System.out.println();
        System.out.println("Where <command> can be one of ");
        values.forEach(command -> System.out.println("- " + command.getName()));
    }

    @Override
    public void printUsage() {
        execute();
    }
}
