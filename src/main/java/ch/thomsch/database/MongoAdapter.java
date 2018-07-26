package ch.thomsch.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.thomsch.metric.Metrics;
import ch.thomsch.model.ClassStore;

import static com.mongodb.client.model.Filters.eq;

/**
 * Represents a connection to a database instance of mongodb. Builds with {@link DatabaseBuilder}.
 */
public class MongoAdapter implements Database{

    private static final Logger logger = LoggerFactory.getLogger(MongoAdapter.class);

    private static final String COLLECTION_REVISION = "revisions";
    private static final String COLLECTION_CLASS = "classes";
    private static final String FIELD_METRICS = "metrics";
    private static final String FIELD_DIFF = "fluctuations";
    private static final String FIELD_REVISION = "revision";
    private static final String FIELD_CLASS_NAME = "name";
    private static final String INDEX_NAME = "compound_revision_name_1";

    private final MongoDatabase database;

    /**
     * Create a new connection to the database.
     * @param database the database
     */
    MongoAdapter(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void persist(HashMap<String, String> ancestry) {
        MongoCollection<Document> revisions = database.getCollection(COLLECTION_REVISION);
        List<Document> documents = createDocuments(ancestry);
        revisions.insertMany(documents);
    }

    @Override
    public void setRaw(ClassStore classStore) {
        setsClassMeasurement(classStore, FIELD_METRICS);
    }

    @Override
    public void setDiff(ClassStore data) {
        setsClassMeasurement(data, FIELD_DIFF);
    }

    private void setsClassMeasurement(ClassStore data, String measurementName) {
        logger.info("Loading collection...");
        final MongoCollection<Document> collection = database.getCollection(COLLECTION_CLASS);

        logger.info("Verifying indexes...");
        verifyIndexes(collection);

        logger.info("Exporting class measurements...");

        List<Document> pendingDocuments = new ArrayList<>();

        for (String revision : data.getVersions()) {
            for (String className : data.getClasses(revision)) {
                final Bson documentFilter = Filters.and(eq(FIELD_REVISION, revision), eq(FIELD_CLASS_NAME, className));

                Document document = collection.find(documentFilter).first();
                if (document == null) {
                    document = createDocument(revision, className, data.getMetric(revision, className),
                            measurementName);
                    pendingDocuments.add(document);
                } else {
                    collection.updateOne(document, new Document("$set",
                            new Document(measurementName, createDocument(data.getMetric(revision, className)))));
                }
            }
        }

        if (!pendingDocuments.isEmpty()) {
            collection.insertMany(pendingDocuments);
        }

        logger.info("Exportation finished");
    }

    private void verifyIndexes(MongoCollection<Document> collection) {
        for (Document document : collection.listIndexes()) {
            if (document.containsKey("name") && document.getString("name").equalsIgnoreCase(INDEX_NAME)) {
                return;
            }
        }

        collection.createIndex(Indexes.ascending(FIELD_REVISION, FIELD_CLASS_NAME),
                new IndexOptions().name(INDEX_NAME));
        logger.info("-> Created missing index '{}'", INDEX_NAME);
    }

    private Document createDocument(String revision, String className, Metrics metrics, String measurementName) {
        Document result = new Document();
        result.append(FIELD_REVISION, revision);
        result.append(FIELD_CLASS_NAME, className);
        result.append(measurementName, createDocument(metrics));
        return result;
    }

    private Document createDocument(Metrics metric) {
        Map<String, Double> metrics = metric.convertToSourceMeterFormat();

        Document result = new Document();
        metrics.forEach(result::append);
        return result;
    }

    private List<Document> createDocuments(HashMap<String, String> ancestry) {
        ArrayList<Document> documents = new ArrayList<>(ancestry.size());
        ancestry.forEach((s, s2) -> documents.add(new Document(FIELD_REVISION, s).append("parent", s2)));
        return documents;
    }
}
