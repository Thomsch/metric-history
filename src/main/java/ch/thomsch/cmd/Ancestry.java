package ch.thomsch.cmd;

import java.io.IOException;
import java.util.List;

import ch.thomsch.model.Genealogy;
import ch.thomsch.storage.GenealogyRepo;
import ch.thomsch.storage.RevisionRepo;
import ch.thomsch.storage.loader.SimpleCommitReader;
import ch.thomsch.versioncontrol.GitVCS;

/**
 * Generates a CSV file containing the list of pairs "version, parent". The version in the first column corresponds
 * to the versions we provide in argument.
 * If a parent version cannot be found for a version, this version will NOT appear in the final file.
 */
public class Ancestry extends Command {
    private String revisionsFile;
    private GitVCS repository;
    private String outputFile;

    @Override
    public String getName() {
        return "ancestry";
    }

    @Override
    public boolean parse(String[] parameters) {
        if (parameters.length < 3) {
            return false;
        }

        revisionsFile = normalizePath(parameters[0]);
        try {
            repository = GitVCS.get(normalizePath(parameters[1]));
        } catch (IOException e) {
            System.out.println("This repository doesn't have version control: " + repository.getDirectory());
        }
        outputFile = normalizePath(parameters[2]);
        return true;
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

    @Override
    public void printUsage() {
        System.out.println("Usage: metric-history ancestry <revision file> <repository path> <output file> ");
        System.out.println();
        System.out.println("<revision file>     is the path to the file containing the revision to analyse.");
        System.out.println("<repository path>   is the path to the folder containing .git folder");
        System.out.println("<output file>       is the path of the file where the results will be stored.");
    }
}
