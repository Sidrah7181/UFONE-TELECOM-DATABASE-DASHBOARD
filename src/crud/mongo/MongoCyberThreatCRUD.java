package crud.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import db.MongoConn;
import model.mongo.CyberThreat;
import org.bson.Document;
import org.bson.types.ObjectId;
import util.MongoValidator;
import util.MongoValidator.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoCyberThreatCRUD.java
 * Full CRUD for 'cyber_threat' collection.
 */
public class MongoCyberThreatCRUD {

    private MongoCollection<Document> col() {
        return MongoConn.getDatabase().getCollection("cyberthreat");
    }

    // ── CREATE ────────────────────────────────────────────────────
    public boolean insert(String attackType, String severityLevel, String status,
                          int affectedTowers, String description, String detectedAt)
            throws ValidationException {

        MongoValidator.validateCyberThreat(attackType, severityLevel, status,
                                            affectedTowers, description, detectedAt);

        Document doc = new Document("attack_type",      attackType)
                .append("severity_level",  severityLevel)
                .append("status",          status)
                .append("affected_towers", affectedTowers)
                .append("description",     description)
                .append("detected_at",     detectedAt);

        col().insertOne(doc);
        return true;
    }

    // ── READ (all) ────────────────────────────────────────────────
    public List<CyberThreat> getAll() {
        List<CyberThreat> list = new ArrayList<>();
        try (MongoCursor<Document> cur = col().find().limit(50).iterator()) {
            while (cur.hasNext()) list.add(docToModel(cur.next()));
        } catch (Exception e) {
            System.err.println("[MongoCyberThreatCRUD] getAll error: " + e.getMessage());
        }
        return list;
    }

    // ── UPDATE (status + description) ────────────────────────────
    public boolean update(String id, String status, String description)
            throws ValidationException {

        if (id == null || id.isBlank())
            throw new ValidationException("No document selected. Click a card first.");

        // Validate updated status field
        java.util.Set<String> validStatus = java.util.Set.of(
            "Detected","Investigating","Mitigated","Resolved");
        if (!validStatus.contains(status))
            throw new ValidationException(
                "Status must be: Detected, Investigating, Mitigated, or Resolved.\n" +
                "You entered: \"" + status + "\"");

        col().updateOne(
            Filters.eq("_id", new ObjectId(id)),
            Updates.combine(
                Updates.set("status",      status),
                Updates.set("description", description)
            )
        );
        return true;
    }

    // ── DELETE ────────────────────────────────────────────────────
    public boolean delete(String id) throws ValidationException {
        if (id == null || id.isBlank())
            throw new ValidationException("No document selected. Click a card first.");
        try {
            col().deleteOne(Filters.eq("_id", new ObjectId(id)));
            return true;
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid document ID format: " + id);
        }
    }

    // ── Helper ────────────────────────────────────────────────────
    private CyberThreat docToModel(Document doc) {
        return new CyberThreat(
            doc.getObjectId("_id").toHexString(),
            doc.getString("attack_type"),
            doc.getString("severitylevel"),
            doc.getString("status"),
            doc.getInteger("affected_towers", 0),
            doc.getString("description"),
            doc.getString("detected_at")
        );
    }
}