package ch.thomsch.database;

import com.mongodb.MongoClient;
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

    private static final String METRICS_FIELD = "metrics";
    private static final String DIFF_FIELD = "fluctuations";
    private static final String REVISION_FIELD = "revision";
    private static final String CLASS_NAME_FIELD = "name";

    private final MongoDatabase database;

    public MongoAdapter() {
        MongoClient mongoClient = new MongoClient();
        database = mongoClient.getDatabase("main");
    }

    @Override
    public void persist(HashMap<String, String> ancestry) {
        MongoCollection<Document> revisions = database.getCollection("revisions");
        List<Document> documents = createDocuments(ancestry);
        revisions.insertMany(documents);
    }

    @Override
    public void setRaw(Raw raw) {
        setsClassMeasurement(raw, METRICS_FIELD);
    }

    @Override
    public void setDiff(Raw data) {
        setsClassMeasurement(data, DIFF_FIELD);
    }

    private void setsClassMeasurement(Raw data, String measurementName) {
        final MongoCollection<Document> collection = database.getCollection("classes");

        List<Document> pendingDocuments = new ArrayList<>();

        for (String revision : data.getVersions()) {
            for (String className : data.getClasses(revision)) {
                final Bson documentFilter = Filters.and(eq(REVISION_FIELD, revision), eq(CLASS_NAME_FIELD, className));

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
        result.append(REVISION_FIELD, revision);
        result.append(CLASS_NAME_FIELD, className);
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
        ancestry.forEach((s, s2) -> documents.add(new Document(REVISION_FIELD, s).append("parent", s2)));
        return documents;
    }
}
