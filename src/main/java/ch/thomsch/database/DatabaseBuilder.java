package ch.thomsch.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseBuilder.class);

    private DatabaseBuilder() {
    }

    /**
     * Builds database with default provider.
     *
     * @param uri          the vendor-specific connection string.
     * @param databaseName the name of the database to connect to.
     * @return a new connection to the database.
     */
    public static Database build(String uri, String databaseName) {
        MongoClient mongoClient;

        if (uri == null) {
            logger.info("Connecting to local database...");
            mongoClient = new MongoClient();
        } else {
            logger.info("Connecting to remote database...");
            final MongoClientURI uri1 = new MongoClientURI(uri);
            mongoClient = new MongoClient(uri1);
        }

        return new MongoAdapter(mongoClient.getDatabase(databaseName));
    }
}
