package ch.thomsch.cmd;

import ch.thomsch.metric.SourceMeterConverter;

/**
 *
 */
public class Convert extends Command {
    private String inputFolder;
    private String outputFile;

    @Override
    public String getName() {
        return "convert";
    }

    @Override
    public boolean parse(String[] parameters) {
        if (parameters.length < 2) {
            return false;
        }

        inputFolder = normalizePath(parameters[0]);
        outputFile = normalizePath(parameters[1]);

        return true;
    }

    @Override
    public void execute() {
        SourceMeterConverter.convert(inputFolder, outputFile);
    }

    @Override
    public void printUsage() {
        System.out.println("Usage: metric-history convert <folder> <output file> ");
        System.out.println();
        System.out.println("<folder>            is the path of the root folder containing the results from the " +
                "third party tool");
        System.out.println("<output file>       is the path of the file where the results will be stored.");
    }
}
