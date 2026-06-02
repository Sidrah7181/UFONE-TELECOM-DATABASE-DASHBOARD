package service;

import crud.mongo.MongoAnomalyCRUD;
import crud.mongo.MongoEventCRUD;
import crud.sql.SqlRegionCRUD;
import crud.sql.SqlTowerCRUD;
import crud.sql.SqlUserCRUD;
import model.mongo.Anomaly;
import model.mongo.EventLog;
import model.sql.Region;
import model.sql.Tower;
import model.sql.User;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchService.java
 *
 * ⚠️ KEY RULE: This service NEVER syncs or merges databases.
 * It queries SQL and MongoDB INDEPENDENTLY, then combines
 * results IN MEMORY into a unified List<SearchResult>.
 *
 * "SearchService does not combine databases — it combines perceptions of databases."
 */
public class SearchService {

    // SQL CRUD instances
    private final SqlUserCRUD   sqlUserCRUD   = new SqlUserCRUD();
    private final SqlTowerCRUD  sqlTowerCRUD  = new SqlTowerCRUD();
    private final SqlRegionCRUD sqlRegionCRUD = new SqlRegionCRUD();

    // Mongo CRUD instances
    private final MongoEventCRUD   mongoEventCRUD   = new MongoEventCRUD();
    private final MongoAnomalyCRUD mongoAnomalyCRUD = new MongoAnomalyCRUD();

    /**
     * Unified search result — wraps any data object with source + type labels.
     */
    public static class SearchResult {
        public final String source; // "SQL" or "MONGO"
        public final String type;   // "User", "Tower", "Region", "EventLog", "Anomaly"
        public final Object data;

        public SearchResult(String source, String type, Object data) {
            this.source = source;
            this.type   = type;
            this.data   = data;
        }
    }

    /**
     * Main search method.
     * Step 1: Query SQL independently.
     * Step 2: Query MongoDB independently.
     * Step 3: Combine into one list in memory.
     * Step 4: Return to UI.
     */
    public List<SearchResult> search(String keyword) {
        List<SearchResult> results = new ArrayList<>();

        if (keyword == null || keyword.isBlank()) return results;

        // ─── SQL QUERIES (independent) ────────────────────────────────────────
        try {
            List<User> users = sqlUserCRUD.searchByName(keyword);
            for (User u : users)
                results.add(new SearchResult("SQL", "User", u));

            List<Tower> towers = sqlTowerCRUD.searchByRegion(keyword);
            for (Tower t : towers)
                results.add(new SearchResult("SQL", "Tower", t));

            List<Region> regions = sqlRegionCRUD.searchByName(keyword);
            for (Region r : regions)
                results.add(new SearchResult("SQL", "Region", r));

        } catch (Exception e) {
            System.err.println("[SearchService] SQL query error: " + e.getMessage());
        }

        // ─── MONGO QUERIES (independent) ─────────────────────────────────────
        try {
            List<EventLog> events = mongoEventCRUD.searchByType(keyword);
            for (EventLog e : events)
                results.add(new SearchResult("MONGO", "EventLog", e));

            List<Anomaly> anomalies = mongoAnomalyCRUD.searchByRisk(keyword);
            for (Anomaly a : anomalies)
                results.add(new SearchResult("MONGO", "Anomaly", a));

        } catch (Exception e) {
            System.err.println("[SearchService] Mongo query error: " + e.getMessage());
        }

        return results;
    }
}
