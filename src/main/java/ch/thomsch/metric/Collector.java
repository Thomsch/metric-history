package ch.thomsch.metric;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.thomsch.versioncontrol.VCS;

/**
 * Uses an {@link Analyzer} on a specific version of a project.
 */
public class Collector {
    private static final Logger logger = LoggerFactory.getLogger(Collector.class);

    private final Analyzer analyzer;

    private final FileFilter filter;
    private final VCS vcs;

    public Collector(Analyzer analyzer, VCS vcs) {
        this.analyzer = analyzer;
        this.vcs = vcs;

        filter = FileFilter.production();
    }

    public void analyzeRevision(String version, String projectDir) {
        if(analyzer.hasInCache(version)){
            return;
        }

        try {
            vcs.checkout(version);
            analyzer.execute(version, projectDir, filter);
            analyzer.postExecute(version);
        } catch (GitAPIException e) {
            logger.error("Failed to load version {} of the project", version);
        }
    }
}
