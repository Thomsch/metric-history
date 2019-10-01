package org.metrichistory.cmd;

import org.metrichistory.model.Genealogy;
import org.metrichistory.storage.GenealogyRepo;
import org.metrichistory.storage.RevisionFile;
import org.metrichistory.storage.loader.SimpleCommitReader;
import org.metrichistory.versioncontrol.VCS;
import org.metrichistory.versioncontrol.VcsBuilder;
import org.metrichistory.versioncontrol.VcsNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @CommandLine.Parameters(index = "0", description = "Path to the file containing the revisions.")
    private String revisionsFile;

    @CommandLine.Parameters(index = "1", description = "Path to the root folder of the version controlled project.")
    private String repositoryPath;

    @CommandLine.Parameters(index = "2", description = "Path of the file where the results will be stored.")
    private String outputFile;

    @Override
    public void run() {
        revisionsFile = normalizePath(revisionsFile);
        outputFile = normalizePath(outputFile);


        final RevisionFile revisionSource = new RevisionFile(new SimpleCommitReader());
        final List<String> revisions = new ArrayList<>();
        try {
            revisions.addAll(revisionSource.load(revisionsFile));
        } catch (FileNotFoundException e) {
            System.err.println(String.format("File '%s' cannot be found.", revisionsFile));
            System.exit(-1);
        } catch (IOException e) {
            System.err.println(String.format("File '%s' cannot be parsed", revisionsFile));
            System.exit(-1);
        }

        try {
            final VCS repository = VcsBuilder.create(normalizePath(repositoryPath));

            final Genealogy genealogy = new Genealogy(repository);
            final GenealogyRepo genealogyRepo = new GenealogyRepo();
            genealogy.addRevisions(revisions);
            genealogyRepo.export(genealogy, outputFile);
        } catch (VcsNotFound e) {
            final String message = String.format("Cannot find repository at '%s'.", repositoryPath);
            System.err.println(message);
            logger.error(message, e);
        } catch (IOException e) {
            final String message = String.format("Couldn't write results on file (%s)", outputFile);
            System.err.println(message);
            logger.error(message, e);
        }
    }
}
