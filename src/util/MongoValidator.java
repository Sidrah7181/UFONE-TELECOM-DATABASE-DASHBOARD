package util;

import db.PostgresConn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * MongoValidator.java
 * Validates MongoDB document fields against schema rules
 * and cross-checks FK references against PostgreSQL.
 */
public class MongoValidator {

    private static final Set<String> RISK_LEVELS     = Set.of("Low","Medium","High","Critical");
    private static final Set<String> SEVERITY_LEVELS = Set.of("Low","Medium","High","Critical");
    private static final Set<String> CYBER_STATUS    = Set.of("Detected","Investigating","Mitigated","Resolved");
    private static final Set<String> EVENT_TYPES     = Set.of(
        "UsageAlert","SignalAlert","CongestionAlert","SecurityAlert",
        "TowerOffline","DegradedMode","HungSession","TrafficAlert",
        "MaintenanceTicketRaised","RecoveryAction","RecoveryUpdate"
    );

    public static class ValidationException extends RuntimeException {
        public ValidationException(String msg) { super(msg); }
    }

    // ── ANOMALY LOG ───────────────────────────────────────────────
   public static void validateAnomaly(String anomalyType, String riskLevel, String towerId, 
                                   String userId, String anomalyTime, String description) throws ValidationException{
    if (anomalyType == null || anomalyType.isBlank()) 
        throw new ValidationException("Anomaly type required");
    if (riskLevel == null || riskLevel.isBlank()) 
        throw new ValidationException("Risk level required");
    if (towerId == null || towerId.isBlank()) 
        throw new ValidationException("Tower ID required");
    if (userId == null || userId.isBlank()) 
        throw new ValidationException("User ID required");
    if (anomalyTime == null || anomalyTime.isBlank()) 
        throw new ValidationException("Anomaly time required");

    }

    // ── CYBER THREAT ──────────────────────────────────────────────
    public static void validateCyberThreat(String attackType, String severity,
                                            String status, int affectedTowers,
                                            String description, String detectedAt)
            throws ValidationException {

        if (attackType == null || attackType.isBlank())
            throw new ValidationException("Attack Type cannot be empty.");

        if (!SEVERITY_LEVELS.contains(severity))
            throw new ValidationException(
                "Severity must be one of: Low, Medium, High, Critical.\nYou entered: \"" + severity + "\"");

        if (!CYBER_STATUS.contains(status))
            throw new ValidationException(
                "Status must be one of: Detected, Investigating, Mitigated, Resolved.\nYou entered: \"" + status + "\"");

        if (affectedTowers < 0)
            throw new ValidationException("Affected Towers must be 0 or more.");

        if (description == null || description.isBlank())
            throw new ValidationException("Description cannot be empty.");

        validateTimestamp(detectedAt, "Detected At");
    }

    // ── EVENT LOG ─────────────────────────────────────────────────
    public static void validateEventLog(String eventType, String towerId,
                                         String userId, String timestamp,
                                         String details)
            throws ValidationException {

        if (eventType == null || eventType.isBlank())
            throw new ValidationException("Event Type cannot be empty.");

        if (towerId == null || !towerId.matches("\\d+"))
            throw new ValidationException("Tower ID must be a positive integer. You entered: \"" + towerId + "\"");

        if (!towerExists(towerId))
            throw new ValidationException("Tower ID " + towerId + " does not exist in the database.");

        if (userId != null && !userId.isBlank()) {
            if (!userId.matches("\\d+"))
                throw new ValidationException("User ID must be a number. You entered: \"" + userId + "\"");
            if (!userExists(userId))
                throw new ValidationException("User ID " + userId + " does not exist in the database.");
        }

        validateTimestamp(timestamp, "Timestamp");

        if (details == null || details.isBlank())
            throw new ValidationException("Details cannot be empty.");
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────

    private static boolean towerExists(String id) {
        return rowExists("SELECT 1 FROM tower WHERE tower_id = ?", Integer.parseInt(id));
    }

    private static boolean userExists(String id) {
        return rowExists("SELECT 1 FROM user_account WHERE user_id = ?", Integer.parseInt(id));
    }

    /**
     * Uses a fresh connection each time to avoid singleton-connection
     * closed-state issues during long sessions.
     */
    private static boolean rowExists(String sql, int param) {
        try (Connection c = PostgresConn.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, param);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("[MongoValidator] DB check failed: " + e.getMessage());
            // If Postgres is unreachable, allow the insert rather than blocking all operations
            return true;
        }
    }

    private static void validateTimestamp(String ts, String fieldName)
            throws ValidationException {
        if (ts == null || ts.isBlank())
            throw new ValidationException(fieldName + " cannot be empty.");
        try {
            LocalDateTime.parse(ts.replace(" ", "T"));
        } catch (Exception e) {
            throw new ValidationException(
                fieldName + " must be in format: yyyy-MM-dd HH:mm:ss\n" +
                "Example: 2026-04-15 10:30:00\nYou entered: \"" + ts + "\"");
        }
    }
}
