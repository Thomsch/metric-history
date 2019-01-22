package ch.thomsch.metric;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.thomsch.versioncontrol.VCS;

/**
 * Uses an {@link Collector} on a specific version of a project.
 */
public class MetricHistory {
    private static final Logger logger = LoggerFactory.getLogger(MetricHistory.class);

    private final Collector collector;

    private final FileFilter filter;
    private final VCS vcs;

    public MetricHistory(Collector collector, VCS vcs) {
        this.collector = collector;
        this.vcs = vcs;

        filter = FileFilter.production();
    }

    public void analyzeRevision(String version, String projectDir) {
        if(collector.hasInCache(version)){
            return;
        }

        try {
            vcs.checkout(version);
            collector.collect(projectDir, version, filter);
            collector.afterCollect(version);
        } catch (GitAPIException e) {
            logger.error("Failed to load version {} of the project", version);
        }
    }
}
