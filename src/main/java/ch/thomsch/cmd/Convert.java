package ch.thomsch.cmd;

import java.io.IOException;

import ch.thomsch.metric.SourceMeterConverter;

/**
 *
 */
public class Convert extends Command {
    private String inputPath;
    private String output;

    @Override
    public String getName() {
        return "convert";
    }

    @Override
    public boolean parse(String[] parameters) {
        if (parameters.length < 2) {
            return false;
        }

        inputPath = normalizePath(parameters[0]);
        output = normalizePath(parameters[1]);

        return true;
    }

    @Override
    public void execute() throws IOException {
        SourceMeterConverter.convert(inputPath, output);
    }

    @Override
    public void printUsage() {
        System.out.println("Usage: metric-history convert <folder> <output file> ");
        System.out.println();
        System.out.println("<folder>            is the path of the root folder containing the results from the " +
                "third party tool");
        System.out.println("<output>       is the path of the file where the results will be stored or a directory. " +
                "In the case of the directory, results will be stored as one file per revision");
    }
}
