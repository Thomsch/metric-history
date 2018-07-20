package ch.thomsch.database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private List<Document> createDocuments(HashMap<String, String> ancestry) {
        ArrayList<Document> documents = new ArrayList<>(ancestry.size());
        ancestry.forEach((s, s2) -> documents.add(new Document("revision", s).append("parent", s2)));
        return documents;
    }
}
