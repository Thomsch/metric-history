package ch.thomsch.storage;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseBuilder.class);

    private DatabaseBuilder() {
    }

    /**
     * Builds storage with default provider.
     *
     * @param uri          the vendor-specific connection string.
     * @param databaseName the name of the storage to connect to.
     * @return a new connection to the storage.
     */
    public static Database build(String uri, String databaseName) {
        final MongoClient mongoClient;

        if (uri == null) {
            logger.info("Connecting to local storage...");
            mongoClient = new MongoClient();
        } else {
            logger.info("Connecting to remote storage...");
            final MongoClientURI uri1 = new MongoClientURI(uri);
            mongoClient = new MongoClient(uri1);
        }

        return new MongoAdapter(mongoClient.getDatabase(databaseName));
    }
}
