package ch.thomsch.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import ch.thomsch.mining.SourceMeterConverter;
import picocli.CommandLine;

/**
 * Converts the results from an analyser to the RAW format.
 */
@CommandLine.Command(
        name = "convert",
        description = "Converts the results from an analyser to the RAW format.")
public class Convert extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Convert.class);

    @CommandLine.Parameters(description = "Path of the root folder containing the results from the third party tool.")
    private String inputPath;

    @CommandLine.Parameters(description = "is the path of the file where the results will be stored or a directory. In the case of the directory, results will be stored as one file per revision.")
    private String output;

    @Override
    public void run() {
        inputPath = normalizePath(inputPath);
        output = normalizePath(output);

        try {
            SourceMeterConverter.convert(inputPath, output);
        } catch (IOException e) {
            logger.error("An error occurred", e);
        }
    }
}
