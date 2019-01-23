package ch.thomsch.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import ch.thomsch.model.Genealogy;
import ch.thomsch.storage.GenealogyRepo;
import ch.thomsch.storage.RevisionRepo;
import ch.thomsch.storage.loader.SimpleCommitReader;
import ch.thomsch.versioncontrol.GitVCS;
import picocli.CommandLine;

/**
 * Generates a CSV file containing the list of pairs "version, parent". The version in the first column corresponds
 * to the versions we provide in argument.
 * If a parent version cannot be found for a version, this version will NOT appear in the final file.
 */

@CommandLine.Command(
        name = "ancestry",
        description = "Find and exports the parents of revisions.")
public class Ancestry extends Command {

    private static final Logger logger = LoggerFactory.getLogger(Ancestry.class);

    @CommandLine.Parameters(description = "Path to the file containing the revisions.")
    private String revisionsFile;

    @CommandLine.Parameters(description = "Path to the folder containing .git folder.")
    private String repositoryPath;

    @CommandLine.Parameters(description = "Path of the file where the results will be stored.")
    private String outputFile;

    private GitVCS repository;

    @Override
    public void run() {
        revisionsFile = normalizePath(revisionsFile);
        outputFile = normalizePath(outputFile);

        try {
            repository = GitVCS.get(normalizePath(repositoryPath));
        } catch (IOException e) {
            logger.error("Cannot find version information in {}", repositoryPath);
        }

        execute();
    }

    @Override
    public void execute() {
        final RevisionRepo revisionRepo = new RevisionRepo(new SimpleCommitReader());
        final Genealogy genealogy = new Genealogy(repository);
        final GenealogyRepo genealogyRepo = new GenealogyRepo();

        final List<String> revisions = revisionRepo.load(revisionsFile);
        genealogy.addRevisions(revisions);
        genealogyRepo.export(genealogy, outputFile);
    }
}
