package ch.thomsch.cmd;

import ch.thomsch.model.Genealogy;
import ch.thomsch.model.vcs.Commit;
import ch.thomsch.model.vcs.Tag;
import ch.thomsch.storage.GenealogyRepo;
import ch.thomsch.storage.RevisionRepo;
import ch.thomsch.storage.loader.SimpleCommitReader;
import ch.thomsch.versioncontrol.VCS;
import ch.thomsch.versioncontrol.VcsBuilder;
import ch.thomsch.versioncontrol.VcsNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.List;

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

    @CommandLine.Parameters(index = "1", description = "Path of the file where the results will be stored.")
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

        List<Tag> releases = repository.listReleases();
        for(Tag tag: releases){
            System.out.println(tag);
        }

        List<Commit> commits = repository.listCommitsBetweenReleases(releases.get(0), releases.get(1));
        for(Commit commit: commits){
            System.out.println(commit);
        }
    }
}
