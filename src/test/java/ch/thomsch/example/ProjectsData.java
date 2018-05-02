package ch.thomsch.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import ch.thomsch.MetricHistory;
import ch.thomsch.export.Reporter;
import ch.thomsch.loader.ZafeirisRefactoringMiner;
import ch.thomsch.metric.CKMetrics;
import ch.thomsch.versioncontrol.GitRepository;

/**
 * @author TSC
 */
public class ProjectsData {
    private static final String BASE_REPOSITORY = "../mined-repositories/";
    private static final String BASE_DATA = "../data/";

    private static final Logger logger = LoggerFactory.getLogger(ProjectsData.class);

    public static void main(String[] args) {
        final MetricHistory metricHistory = new MetricHistory(
                new CKMetrics(),
                new Reporter(),
                new ZafeirisRefactoringMiner());

        collectProject(metricHistory, "dagger");
        collectProject(metricHistory, "dagger2");
        collectProject(metricHistory, "jena");
        collectProject(metricHistory, "jfreechart");
        collectNeo4j(metricHistory, "neo4j_HEAD_2.3.9");
        collectNeo4j(metricHistory, "neo4j_2.3.9_pre_1.9.9");
        collectNeo4j(metricHistory, "neo4j_pre_1.9.9_to_1.9.9");
        collectNeo4j(metricHistory, "neo4j_1.9.9_start");
    }

    private static void collectNeo4j(MetricHistory metricHistory, String sliceName) {
        try {
            metricHistory.collect(getRevisionsLocation(sliceName), getRepository("neo4j"), getOutputFile(sliceName));
        } catch (IOException e) {
            logger.error("Repository not found for project {}", sliceName, e);
        }
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
        return BASE_DATA + "metrics/" + projectName + "-metrics.csv";
    }

    private static GitRepository getRepository(String projectName) throws IOException {
        return GitRepository.get(BASE_REPOSITORY + projectName);
    }

    private static String getRevisionsLocation(String projectName) {
        return BASE_DATA + "/revisions/" + projectName + ".csv";
    }
}
