package ch.thomsch.cmd;

import ch.thomsch.storage.loader.SimpleCommitReader;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import ch.thomsch.storage.loader.RefactoringMiner;
import ch.thomsch.versioncontrol.GitRepository;

/**
 *
 */
public class Ancestry extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Ancestry.class);

    private String revisionFile;
    private GitRepository repository;
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

        revisionFile = normalizePath(parameters[0]);
        try {
            repository = GitRepository.get(normalizePath(parameters[1]));
        } catch (IOException e) {
            System.out.println("This repository doesn't have version control: " + repository.getDirectory());
        }
        outputFile = normalizePath(parameters[2]);
        return true;
    }

    @Override
    public void execute() {
        final ch.thomsch.Ancestry ancestry = new ch.thomsch.Ancestry(repository, new SimpleCommitReader());
        ancestry.make(revisionFile);

        try (CSVPrinter writer = ancestry.getPrinter(outputFile)) {
            ancestry.export(writer);
        } catch (IOException e) {
            logger.error("I/O error with file {}", outputFile, e);
        }
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
