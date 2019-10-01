package org.metrichistory.cmd;

import org.metrichistory.model.vcs.Commit;
import org.metrichistory.model.vcs.Tag;
import org.metrichistory.storage.export.Reporter;
import org.metrichistory.versioncontrol.VCS;
import org.metrichistory.versioncontrol.VcsBuilder;
import org.metrichistory.versioncontrol.VcsNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import picocli.CommandLine;

/**
 * Generates a detailed description of the project's versions, quantifying it's duration and number of commits.
 */

@CommandLine.Command(
        name = "release-history",
        description = "Exports information on the entire revision history of the repository")
public class ReleaseHistory extends Command {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseHistory.class);

    @CommandLine.Parameters(index = "0", description = "Path to the root folder of the version controlled project.")
    private String repositoryPath;

    @CommandLine.Parameters(index = "1", description = "File with an ordered list of tags to process")
    private String tagListFile;

    @CommandLine.Parameters(index = "2", description = "Master branch name.")
    private String masterBranchName = "master";


    @CommandLine.Parameters(index = "3", description = "Path of the file where the results will be stored.")
    private String outputFile;

    @Override
    public void run() {
        outputFile = normalizePath(outputFile);
        tagListFile = normalizePath(tagListFile);


        final List<String> tagList;
        try {
            tagList = getTags(tagListFile);
        } catch (IOException e) {
            final String message = String.format("Failed to read tags in file '%s'", tagListFile);
            System.err.println(message);
            logger.error(message, e);
            return;
        }

        try(VCS repository = VcsBuilder.create(normalizePath(repositoryPath))) {
            Tag.setMasterBranch(masterBranchName);

            final List<Tag> releases = repository.listSelectedReleases(tagList);

            // exclude first release
            releases.remove(0);

            final Reporter reporter = new Reporter();
            reporter.initialize(outputFile);
            reporter.report(new Object[]{"revision", "commitDate", "commitSequence",
                    "commitsToRelease", "commitCount",
                    "daysToRelease", "releaseDuration",
                    "release"});

            releases.stream()
                    .flatMap((Function<Tag, Stream<Commit>>) tag -> {
                        if (tag.isMasterRef()) {
                            return Stream.empty();
                        }
                        return repository.listCommitsBetweenReleases(tag, tag.getNextTag())
                                .stream();
                    })
                    .map(commit -> {
                        Object[] lineItems = new Object[]{
                                commit.getId(),
                                commit.getDate().toLocalDate(),
                                commit.getCommitSequence(),
                                commit.getCommitsToNextRelease(),
                                commit.getCommitSequence() + commit.getCommitsToNextRelease(),
                                commit.getDaysToNextRelease(),
                                commit.getNextReleaseDuration(),
                                commit.getNextRelease().getTagRef()
                        };
                        return lineItems;
                    })
                    .forEach(lineItems -> {
                        try {
                            reporter.report(lineItems);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            reporter.finish();

            for(Tag tag: releases){
                System.out.println(tag);
            }

        } catch (VcsNotFound e) {
            final String message = String.format("Directory '%s' cannot be interpreted as a version control project", repositoryPath);
            System.err.println(message);
            logger.error(message, repositoryPath);
        } catch (IOException e) {
            final String message = String.format("Couldn't write on output file (%s)", outputFile);
            System.err.println(message);
            logger.error(message, e);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An unknown error occurred while accessing the repository", e);
        }
    }

    private List<String> getTags(String tagListFile) throws IOException {
        List<String> tagList;
        try (Stream<String> lines = Files.lines(Paths.get(tagListFile))) {
            tagList = lines.collect(Collectors.toList());
        }
        return tagList;
    }
}
