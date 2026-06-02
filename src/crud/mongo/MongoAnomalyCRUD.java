package crud.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import db.MongoConn;
import model.mongo.Anomaly;
import org.bson.Document;
import org.bson.types.ObjectId;
import util.MongoValidator;
import util.MongoValidator.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class MongoAnomalyCRUD {

    private MongoCollection<Document> col() {
        return MongoConn.getDatabase().getCollection("anomalylog");
    }

    // Matches your Anomaly constructor: anomalyType, riskLevel, towerId, userId, anomalyTime, description
    public boolean insert(String anomalyType, String riskLevel, String towerId, 
                         String userId, String anomalyTime, String description) throws ValidationException {
        
        MongoValidator.validateAnomaly(anomalyType, riskLevel, towerId, userId, anomalyTime, description);
        
        try {
            Document doc = new Document("anomaly_type", anomalyType)
                .append("risk_level", riskLevel)
                .append("tower_id", towerId)
                .append("user_id", userId)
                .append("anomaly_time", anomalyTime)
                .append("description", description);
            col().insertOne(doc);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("MongoDB insert failed: " + e.getMessage(), e);
        }
    }

    public List<Anomaly> getAll() {
        List<Anomaly> list = new ArrayList<>();
        try (MongoCursor<Document> cur = col().find().limit(50).iterator()) {
            while (cur.hasNext()) list.add(docToModel(cur.next()));
        } catch (Exception e) {
            System.err.println("[MongoAnomalyCRUD] getAll error: " + e.getMessage());
        }
        return list;
    }

    public List<Anomaly> searchByRisk(String keyword) {
        List<Anomaly> list = new ArrayList<>();
        try (MongoCursor<Document> cur = col()
            .find(Filters.regex("risk_level", keyword, "i"))
            .limit(20).iterator()) {
            while (cur.hasNext()) list.add(docToModel(cur.next()));
        } catch (Exception e) {
            System.err.println("[MongoAnomalyCRUD] search error: " + e.getMessage());
        }
        return list;
    }

    public boolean update(String id, String riskLevel, String description) throws ValidationException {
        try {
            col().updateOne(
                Filters.eq("_id", new ObjectId(id)),
                Updates.combine(
                    Updates.set("risk_level", riskLevel),
                    Updates.set("description", description)
                )
            );
            return true;
        } catch (Exception e) {
            System.err.println("[MongoAnomalyCRUD] update error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id) {
        try {
            col().deleteOne(Filters.eq("_id", new ObjectId(id)));
            return true;
        } catch (Exception e) {
            System.err.println("[MongoAnomalyCRUD] delete error: " + e.getMessage());
            return false;
        }
    }

    private Anomaly docToModel(Document doc) {
        return new Anomaly(
            doc.getObjectId("_id").toHexString(),
            doc.getString("anomaly_type"),
            doc.getString("risk_level"),
            String.valueOf(doc.get("tower_id")),
            String.valueOf(doc.get("user_id")),
            doc.getString("anomalytime"),
            doc.getString("description")
        );
    }
}