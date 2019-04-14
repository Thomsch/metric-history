package ch.thomsch.cmd;

import ch.thomsch.model.vcs.Commit;
import ch.thomsch.model.vcs.Tag;
import ch.thomsch.storage.export.Reporter;
import ch.thomsch.versioncontrol.VCS;
import ch.thomsch.versioncontrol.VcsBuilder;
import ch.thomsch.versioncontrol.VcsNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates a CSV file containing the list of pairs "version, parent". The version in the first column corresponds
 * to the versions we provide in argument.
 * If a parent version cannot be found for a version, this version will NOT appear in the final file.
 */

@CommandLine.Command(
        name = "revision-history",
        description = "Exports information on the entire revision history of the repository")
public class RevisionHistory extends Command {

    private static final Logger logger = LoggerFactory.getLogger(RevisionHistory.class);

    @CommandLine.Parameters(index = "0", description = "Path to the root folder of the version controlled project.")
    private String repositoryPath;

    @CommandLine.Parameters(index = "1", description = "File with an ordered list of tags to process")
    private String tagListFile;

    @CommandLine.Parameters(index = "2", description = "Master branch name.")
    private String masterBranchName = "master";


    @CommandLine.Parameters(index = "3", description = "Path of the file where the results will be stored.")
    private String outputFile;

    private VCS repository;

    @Override
    public void run() {

        outputFile = normalizePath(outputFile);
        try {
            repository = VcsBuilder.create(normalizePath(repositoryPath));
        } catch (VcsNotFound e) {
            logger.error("Cannot find version information in {}", repositoryPath);
        }

        Tag.setMasterBranch(masterBranchName);

        tagListFile = normalizePath(tagListFile);



        Reporter reporter = new Reporter();
        try {

            // get tag list from files
            List<String> tagList;
            try (Stream<String> lines = Files.lines(Paths.get(tagListFile))) {
                tagList = lines.collect(Collectors.toList());
            }

            List<Tag> releases = repository.listSelectedReleases(tagList);

            // exclude first release
            releases.remove(0);

            reporter.initialize(outputFile);

            reporter.report(new Object[]{"revision", "commitDate", "commitSequence",
                    "commitsToNextRelease", "daysToNextRelease", "nextRelease"});

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
                                commit.getDaysToNextRelease(),
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
        } catch (IOException e) {
            logger.error("Output file error", e);
            return;
        }
    }
}
