package org.metrichistory.analyzer;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.metrichistory.mining.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Collects metrics from the command line.
 */
public class SourceMeter implements Analyzer {
    private static final Logger logger = LoggerFactory.getLogger(SourceMeter.class);

    private final String projectName;
    private final File rootOutputDirectory;
    private final CommandLine commandLine;
    private final Map<String, Object> map = new HashMap<>();

    public SourceMeter(String executable, String resultDir, String projectName, String projectDir) {
        this.projectName = projectName;
        resultDir = FilenameUtils.normalize(resultDir);

        commandLine = new CommandLine(executable);
        enableOnlyMetrics();
        commandLine.addArgument("-projectName=" + projectName);
        commandLine.addArgument("-projectBaseDir=" + projectDir);
        commandLine.addArgument("-cleanProject=" + true);
        commandLine.addArgument("-resultsDir=" + resultDir);
        commandLine.addArgument("${currentDate}");
        commandLine.setSubstitutionMap(map);

        rootOutputDirectory = new File(resultDir, projectName + File.separatorChar + "java");
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
    public void execute(String revision, String folder, FileFilter filter) {
        final DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler(new LogOutputStream() {
            @Override
            protected void processLine(String line, int logLevel) {
                System.out.println(line);
            }
        }));
        final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        map.put("currentDate", "-currentDate=" + revision);

        try {
            executor.execute(commandLine, resultHandler);
            resultHandler.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postExecute(String version) {
        final File output = new File(rootOutputDirectory, version);

        try {
            deleteFile(output, projectName + ".graph");
            deleteFile(output, projectName + ".xml");
            FileUtils.deleteDirectory(new File(output, "sourcemeter"));
        } catch (IOException e) {
            logger.error("An error occurred while cleaning up version " + version, e);
        }
    }

    @Override
    public boolean hasInCache(String version) {
        final File output = new File(rootOutputDirectory, version);
        return output.exists();
    }

    private void deleteFile(File baseDir, String file) throws IOException {
        final File currentFile = new File(baseDir, file);
        final boolean result = Files.deleteIfExists(currentFile.toPath());
        if (!result) {
            logger.error("File " + currentFile.getAbsolutePath() + " could not be deleted.");
        }
    }
}
