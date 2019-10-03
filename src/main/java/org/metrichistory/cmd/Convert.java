package org.metrichistory.cmd;

import org.metrichistory.analyzer.sourcemeter.SourceMeterConverter;
import org.metrichistory.model.FormatException;
import org.metrichistory.storage.DirectoryCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;

/**
 * Converts the results from an analyser to the RAW format.
 */
@CommandLine.Command(
        name = "convert",
        description = "Converts the results from an analyser to the RAW format.")
public class Convert extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Convert.class);

    @CommandLine.Parameters(index = "0", description = "Path of the root folder containing the results from the third party tool.")
    private String inputPath;

    @CommandLine.Parameters(index = "1", description = "is the path of the file where the results will be stored or a directory. In the case of the directory, results will be stored as one file per revision.")
    private String output;

    @Override
    public void run() {
        inputPath = normalizePath(inputPath);
        output = normalizePath(output);

        try {
            SourceMeterConverter.convert(inputPath, output);
        } catch (DirectoryCreationException e) {
            System.err.println(String.format("The output directory '%s' cannot be created", output));
            logger.error(e.getMessage(), e);
        } catch (FormatException e) {
            System.err.println(String.format("'%s' is not a valid Source Meter directory", inputPath));
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            System.err.println(String.format("An writing or reading error on a file occurred: %s", e.getMessage()));
            logger.error(e.getMessage(), e);
        }
    }
}
