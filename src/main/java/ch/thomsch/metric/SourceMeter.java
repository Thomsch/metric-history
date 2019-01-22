package ch.thomsch.metric;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.util.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import ch.thomsch.model.MetricDump;

/**
 * Collects metrics from the command line.
 *
 * @author Thomsch
 */
public class SourceMeter implements Analyzer {
    private static final Logger logger = LoggerFactory.getLogger(SourceMeter.class);

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private final CommandLine commandLine;

    private final Map<String, Object> map = new HashMap<>();
    private final String resultDir;
    private final String projectName;

    public SourceMeter(String executable, String resultDir, String projectName, String projectDir) {
        this.projectName = projectName;
        this.resultDir = FilenameUtils.normalize(resultDir);

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
    public MetricDump collect(String folder, String revision, FileFilter filter) {
        final DefaultExecutor executor = new DefaultExecutor();
        final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        map.put("currentDate", "-currentDate=" + revision);

        try {
            executor.execute(commandLine, resultHandler);
            resultHandler.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return MetricDump.EMPTY;
    }

    @Override
    public void afterCollect(String revision) {
        final String baseDir = Paths.stripTrailingSeparator(resultDir) + File.separatorChar + projectName + File
                .separatorChar + "java" + File.separatorChar + revision;

        try {
            deleteFile(baseDir, projectName + ".graph");
            deleteFile(baseDir, projectName + ".xml");
            FileUtils.deleteDirectory(new File(baseDir + File.separatorChar + "sourcemeter"));
        } catch (IOException e) {
            logger.error("An error occurred while cleaning up revision " + revision, e);
        }
    }

    @Override
    public boolean hasInCache(String version) {
        return false;
    }

    private void deleteFile(String baseDir, String file) throws IOException {
        final File currentFile = new File(baseDir + File.separatorChar + file);
        final boolean result = Files.deleteIfExists(currentFile.toPath());
        if (!result) {
            logger.error("File " + currentFile.getAbsolutePath() + " could not be deleted.");
        }
    }

    public BufferedReader getOutput() {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray())));
    }
}
