package ch.thomsch.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.thomsch.metric.Metric;
import ch.thomsch.model.Raw;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author Thomsch
 */
public class MongoAdapter implements Database{

    private static final String COLLECTION_REVISION = "revisions";
    private static final String COLLECTION_CLASS = "classes";
    private static final String FIELD_METRICS = "metrics";
    private static final String FIELD_DIFF = "fluctuations";
    private static final String FIELD_REVISION = "revision";
    private static final String FIELD_CLASS_NAME = "name";

    private final MongoDatabase database;

    /**
     * Create a new connection to the database
     *
     * @param uri a {@link MongoClientURI} or null to connect in local.
     *            <p>Example: mongodb+srv://username:password@host/test?retryWrites=true</p>
     */
    public MongoAdapter(String uri) {
        MongoClient mongoClient;

        if (uri == null) {
            mongoClient = new MongoClient();
        } else {
            mongoClient = new MongoClient(new MongoClientURI(uri));
        }

        database = mongoClient.getDatabase("main");
    }

    @Override
    public void persist(HashMap<String, String> ancestry) {
        MongoCollection<Document> revisions = database.getCollection(COLLECTION_REVISION);
        List<Document> documents = createDocuments(ancestry);
        revisions.insertMany(documents);
    }

    @Override
    public void setRaw(Raw raw) {
        setsClassMeasurement(raw, FIELD_METRICS);
    }

    @Override
    public void setDiff(Raw data) {
        setsClassMeasurement(data, FIELD_DIFF);
    }

    private void setsClassMeasurement(Raw data, String measurementName) {
        final MongoCollection<Document> collection = database.getCollection(COLLECTION_CLASS);

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
        collection.insertMany(pendingDocuments);
    }

    private Document createDocument(String revision, String className, Metric metric, String measurementName) {
        Document result = new Document();
        result.append(FIELD_REVISION, revision);
        result.append(FIELD_CLASS_NAME, className);
        result.append(measurementName, createDocument(metric));
        return result;
    }

    private Document createDocument(Metric metric) {
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
