package org.metrichistory.cmd;

import org.metrichistory.model.MeasureStore;
import org.metrichistory.storage.Database;
import org.metrichistory.storage.DatabaseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

import org.metrichistory.storage.GenealogyRepo;
import org.metrichistory.storage.Stores;
import picocli.CommandLine;

/**
 * Exports different files produced by this application to mongodb.
 */

@CommandLine.Command(
        name = "mongo",
        description = "Exports different files produced by this application to mongodb.")
public class Mongo extends Command {
    private static final Logger logger = LoggerFactory.getLogger(Mongo.class);

    @CommandLine.Parameters(index = "0", description = "Type of data to export to mongo.")
    private String action;

    @CommandLine.Parameters(index = "1", description = "Path of the file to export.")
    private String file;

    @CommandLine.Parameters(index = "2", description = "Name of the database (storage name).")
    private String databaseName;

    @CommandLine.Parameters(index = "3", arity = "0..1", description = "URI of the database")
    private String connectionString;

    @Override
    public void run() {
        file = normalizePath(file);

        final Database database = DatabaseBuilder.build(connectionString, databaseName);

        try {
            switch (action) {
                case "raw":
                    final MeasureStore raw = Stores.loadClasses(file);
                    database.setRaw(raw);
                    break;

                case "diff":
                    final MeasureStore diff = Stores.loadClasses(file);
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
            }
        } catch (IOException e) {
            logger.error("I/O error with file {}", file, e);
        }
    }
}
