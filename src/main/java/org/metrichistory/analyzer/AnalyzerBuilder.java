package org.metrichistory.analyzer;

import org.metrichistory.analyzer.ck.CKMetrics;
import org.metrichistory.analyzer.sourcemeter.SourceMeter;

import java.util.Objects;

/**
 * From an instance of {@link Census}, builds a corresponding instance of {@link Analyzer}.
 */
public class AnalyzerBuilder {

    private String outputDirectory;
    private String executable;
    private String projectName;
    private String inputDirectory;

    public Analyzer build(Census analyzerId) {
        Objects.requireNonNull(analyzerId);
        switch (analyzerId) {
            case SOURCEMETER:
                Objects.requireNonNull(executable, "An executable path is required for this analyzer");
                Objects.requireNonNull(inputDirectory);
                Objects.requireNonNull(outputDirectory);
                Objects.requireNonNull(projectName);

                return new SourceMeter(executable, outputDirectory, projectName, inputDirectory);
            case CK:
                return new CKMetrics();
        }
        throw new IllegalArgumentException(String.format("'%s' is not a supported analyzer. Verify spelling.", analyzerId));
    }

    public void setExecutable(String path) {
        this.executable = path;
    }

    public void setOutputDirectory(String folder) {
        this.outputDirectory = folder;
    }

    public void setInputDirectory(String folder) {
        this.inputDirectory = folder;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public enum Census {SOURCEMETER, CK}
}
