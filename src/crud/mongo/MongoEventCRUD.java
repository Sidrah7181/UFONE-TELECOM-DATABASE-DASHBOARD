package crud.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import db.MongoConn;
import model.mongo.EventLog;
import org.bson.Document;
import org.bson.types.ObjectId;
import util.MongoValidator;
import util.MongoValidator.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoEventCRUD.java
 * Full CREATE / READ / UPDATE / DELETE for 'event_log' collection.
 * Now with Postgres FK validation.
 */
public class MongoEventCRUD {

   private MongoCollection<Document> col() {
    return MongoConn.getDatabase().getCollection("eventlog"); 
}

    // ── CREATE ────────────────────────────────────────────────────────────────
    public boolean insert(String eventType, String towerId, String userId,
                          String timestamp, String details) throws ValidationException {
        
        MongoValidator.validateEventLog(eventType, towerId, userId, timestamp, details);
        
        try {
            Document doc = new Document("event_type", eventType)
                   .append("tower_id", towerId)
                   .append("user_id", userId)
                   .append("timestamp", timestamp)
                   .append("details", details);
            col().insertOne(doc);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("MongoDB insert failed: " + e.getMessage(), e);
        }
    }

    // ── READ (all, limit 50) ──────────────────────────────────────────────────
    public List<EventLog> getAll() {
        List<EventLog> list = new ArrayList<>();
        try (MongoCursor<Document> cur = col().find().limit(50).iterator()) {
            while (cur.hasNext()) list.add(docToModel(cur.next()));
        } catch (Exception e) {
            System.err.println("[MongoEventCRUD] getAll error: " + e.getMessage());
        }
        return list;
    }

    // ── READ (search by event_type) ───────────────────────────────────────────
    public List<EventLog> searchByType(String keyword) {
        List<EventLog> list = new ArrayList<>();
        try (MongoCursor<Document> cur = col()
               .find(Filters.regex("event_type", keyword, "i"))
               .limit(20).iterator()) {
            while (cur.hasNext()) list.add(docToModel(cur.next()));
        } catch (Exception e) {
            System.err.println("[MongoEventCRUD] search error: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE (update details + event_type by _id) ────────────────────────────
    public boolean update(String id, String newDetails, String newEventType) {
        try {
            col().updateOne(
                Filters.eq("_id", new ObjectId(id)),
                Updates.combine(
                    Updates.set("details", newDetails),
                    Updates.set("event_type", newEventType)
                )
            );
            return true;
        } catch (Exception e) {
            System.err.println("[MongoEventCRUD] update error: " + e.getMessage());
            return false;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean delete(String id) {
        try {
            col().deleteOne(Filters.eq("_id", new ObjectId(id)));
            return true;
        } catch (Exception e) {
            System.err.println("[MongoEventCRUD] delete error: " + e.getMessage());
            return false;
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private EventLog docToModel(Document doc) {
    return new EventLog(
        doc.getObjectId("_id").toHexString(),
        doc.getString("event_type"),
        String.valueOf(doc.get("tower_id")),
        String.valueOf(doc.get("user_id")),
        doc.getString("timestamp"),
        doc.getString("details")
    );
}
}