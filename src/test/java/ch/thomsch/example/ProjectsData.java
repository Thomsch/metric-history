package ch.thomsch.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import ch.thomsch.Collector;
import ch.thomsch.MetricHistory;
import ch.thomsch.ModifiedRMinerReader;
import ch.thomsch.Reporter;
import ch.thomsch.versioncontrol.GitRepository;

/**
 * @author TSC
 */
public class ProjectsData {
    private static final String BASE = "../mined-repositories/";

    private static final Logger logger = LoggerFactory.getLogger(ProjectsData.class);

    public static void main(String[] args) {
        final MetricHistory metricHistory = new MetricHistory(
                new Collector(),
                new Reporter(),
                new ModifiedRMinerReader());


//        collectProject(metricHistory, "dagger");
        collectProject(metricHistory, "dagger2");
        collectProject(metricHistory, "jena");
        collectProject(metricHistory, "jfreechart");
    }

    private static void collectProject(MetricHistory metricHistory, String projectName) {
        try {
            metricHistory.collect(getRevisionsLocation(projectName), getRepository(projectName), getOutputFile
                    (projectName));
        } catch (IOException e) {
            logger.error("Repository not found for project {}", projectName, e);
        }
    }

    private static String getOutputFile(String projectName) {
        return BASE + "results/" + projectName + "-metrics.csv";
    }

    private static GitRepository getRepository(String projectName) throws IOException {
        return GitRepository.get(BASE + projectName);
    }

    private static String getRevisionsLocation(String projectName) {
        return BASE + projectName + ".csv";
    }
}
