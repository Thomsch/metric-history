package ch.thomsch.database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.thomsch.metric.Metric;
import ch.thomsch.model.Raw;

/**
 * @author Thomsch
 */
public class MongoAdapter implements Database{

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
    public void persistRaw(Raw raw) {
        final MongoCollection<Document> collection = database.getCollection("classes");

        if (collection.countDocuments() > 0) {
            collection.drop();
        }

        List<Document> documents = createDocuments(raw);
        collection.insertMany(documents);
    }

    private List<Document> createDocuments(Raw raw) {
        ArrayList<Document> documents = new ArrayList<>();
        for (String revision : raw.getVersions()) {
            for (String className : raw.getClasses(revision)) {
                Document document = new Document("name", className)
                        .append("revision", revision)
                        .append("metrics", createDocument(raw.getMetric(revision, className)));

                documents.add(document);
            }
        }
        return documents;
    }

    private Document createDocument(Metric metric) {
        Map<String, Double> metrics = metric.convertToSourceMeterFormat();

        Document result = new Document();
        metrics.forEach(result::append);
        return result;
    }

    private List<Document> createDocuments(HashMap<String, String> ancestry) {
        ArrayList<Document> documents = new ArrayList<>(ancestry.size());
        ancestry.forEach((s, s2) -> documents.add(new Document("revision", s).append("parent", s2)));
        return documents;
    }
}
