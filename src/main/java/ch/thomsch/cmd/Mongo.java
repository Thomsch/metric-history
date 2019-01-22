package ch.thomsch.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

import ch.thomsch.model.ClassStore;
import ch.thomsch.storage.Database;
import ch.thomsch.storage.DatabaseBuilder;
import ch.thomsch.storage.GenealogyRepo;
import ch.thomsch.storage.Stores;

public class Mongo extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Mongo.class);

    private String action;
    private String file;
    private String databaseName;
    private String connectionString;

    @Override
    public String getName() {
        return "mongo";
    }

    @Override
    public boolean parse(String[] parameters) {
        if (parameters.length < 3) {
            return false;
        }

        action = parameters[0];
        this.file = normalizePath(parameters[1]);
        databaseName = parameters[2];
        connectionString = parameters.length == 4 ? parameters[3] : null;

        return true;
    }

    @Override
    public void execute() {
        final Database database = DatabaseBuilder.build(connectionString, databaseName);

        try {
            switch (action) {
                case "raw":
                    final ClassStore raw = Stores.loadClasses(file);
                    database.setRaw(raw);
                    break;

                case "diff":
                    final ClassStore diff = Stores.loadClasses(file);
                    database.setDiff(diff);
                    break;

                case "ancestry":

                    final GenealogyRepo repo = new GenealogyRepo();
                    final HashMap<String, String> ancestry = repo.load(file);

                    if (ancestry.isEmpty()) {
                        logger.warn("No ancestry was found...");
                        return;
                    }

                    database.persist(ancestry);
                    break;

                default:
                    printUsage();
                    break;
            }
        } catch (IOException e) {
            logger.error("I/O error with file {}", file, e);
        }
    }

    @Override
    public void printUsage() {
        System.out.println("Usages:");
        System.out.println("     metric-history mongo raw <raw file> <storage name> [remote URI]");
        System.out.println("     metric-history mongo diff <diff file> <storage name> [remote URI]");
        System.out.println("     metric-history mongo ancestry <ancestry file> <storage name> [remote URI]");
    }
}
