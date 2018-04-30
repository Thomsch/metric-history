package ch.thomsch.metric;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.NotImplementedException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.thomsch.Metric;

/**
 * Collects metrics from the command line.
 *
 * @author TSC
 */
public class SourceMeter implements Collector {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private final CommandLine commandLine;

    private final Map<String, Object> map = new HashMap<>();

    public SourceMeter(String executable, String resultDir, String projectName, String projectDir) {
        resultDir = FilenameUtils.normalize(resultDir);

        commandLine = new CommandLine(executable);
        enableOnlyMetrics();

        commandLine.addArgument("-projectName=" + projectName);
        commandLine.addArgument("-projectBaseDir=" + projectDir);
        commandLine.addArgument("-cleanProject=" + true);
        commandLine.addArgument("-resultsDir=" + resultDir);
        commandLine.addArgument("${currentDate}");
        commandLine.setSubstitutionMap(map);
    }

    private void enableOnlyMetrics() {
        commandLine.addArgument("-runAndroidHunter=false");
        commandLine.addArgument("-runMetricHunter=false");
        commandLine.addArgument("-runVulnerabilityHunter=false");
        commandLine.addArgument("-runFaultHunter=false");
        commandLine.addArgument("-runRTEHunter=false");
        commandLine.addArgument("-runDCF=false");
        commandLine.addArgument("-runFB=false");
        commandLine.addArgument("-runPMD=false");
    }

    @Override
    public Metric collect(String folder) {
        throw new NotImplementedException("Functionality not available");
    }

    @Override
    public Metric collect(String folder, Collection<File> files, String revision) {
        final DefaultExecutor executor = new DefaultExecutor();
        final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        map.put("currentDate", "-currentDate=" + revision);

        try {
//            executor.setStreamHandler(new PumpStreamHandler(outputStream));
            executor.execute(commandLine, resultHandler);
            resultHandler.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Metric();
    }

    public BufferedReader getOutput() {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray())));
    }
}
