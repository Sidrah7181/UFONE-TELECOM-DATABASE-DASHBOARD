package util;

import com.mongodb.client.MongoCollection;
import db.MongoConn;
import org.bson.Document;

import java.util.List;

/**
 * MongoSeeder.java
 * Run this ONCE to seed the 3 MongoDB collections with data
 * that matches your PostgreSQL dataset.
 *
 * HOW TO RUN: Add a call to MongoSeeder.seed() in your Main.java
 * just after the DB connections are tested, then remove it after
 * the first successful run.
 *
 * Example in Main.java:
 *   if (MongoConn.testConnection()) {
 *       MongoSeeder.seed();   // ← add this, run once, then remove
 *   }
 */
public class MongoSeeder {

    public static void seed() {
        seedEventLog();
        seedAnomalyLog();
        seedCyberThreat();
        System.out.println("[MongoSeeder] Seeding complete.");
    }

    private static void seedEventLog() {
        MongoCollection<Document> col = MongoConn.getDatabase().getCollection("eventlog");
        if (col.countDocuments() > 0) {
            System.out.println("[MongoSeeder] eventlog already has data — skipping.");
            return;
        }
        col.insertMany(List.of(
            new Document("event_type", "UsageAlert")
                .append("tower_id", "1").append("user_id", "2")
                .append("timestamp", "2026-04-10 11:01:00")
                .append("details", "{\"threshold_mb\":1500,\"actual_mb\":1820,\"action\":\"flagged\",\"user\":\"Sara Khan\"}"),
            new Document("event_type", "SignalAlert")
                .append("tower_id", "2").append("user_id", null)
                .append("timestamp", "2026-04-10 12:01:00")
                .append("details", "{\"before_dbm\":-52.0,\"after_dbm\":-84.0,\"duration_sec\":180}"),
            new Document("event_type", "MaintenanceTicketRaised")
                .append("tower_id", "2").append("user_id", null)
                .append("timestamp", "2026-04-10 12:15:00")
                .append("details", "{\"ticket_no\":\"MNT-20260410-002\",\"team\":\"Alpha Maintenance Unit\"}"),
            new Document("event_type", "CongestionAlert")
                .append("tower_id", "30").append("user_id", null)
                .append("timestamp", "2026-04-11 08:01:00")
                .append("details", "{\"max_capacity\":650,\"active_users\":632,\"load_pct\":97.2}"),
            new Document("event_type", "SecurityAlert")
                .append("tower_id", "3").append("user_id", "9")
                .append("timestamp", "2026-04-12 12:01:00")
                .append("details", "{\"blocked_user_id\":9,\"action\":\"connection_denied\"}"),
            new Document("event_type", "TowerOffline")
                .append("tower_id", "35").append("user_id", null)
                .append("timestamp", "2026-04-15 08:01:00")
                .append("details", "{\"last_ping\":\"2026-04-14 23:45:00\",\"action\":\"recovery_team_dispatched\"}"),
            new Document("event_type", "DegradedMode")
                .append("tower_id", "22").append("user_id", null)
                .append("timestamp", "2026-04-15 10:01:00")
                .append("details", "{\"current_mode\":\"Degraded\",\"maintenance_eta\":\"2026-04-16 18:00:00\"}"),
            new Document("event_type", "CongestionAlert")
                .append("tower_id", "4").append("user_id", null)
                .append("timestamp", "2026-04-15 20:31:00")
                .append("details", "{\"max_capacity\":600,\"active_users\":598,\"load_pct\":99.7}"),
            new Document("event_type", "HungSession")
                .append("tower_id", "5").append("user_id", "11")
                .append("timestamp", "2026-04-15 20:01:00")
                .append("details", "{\"session_log_id\":40,\"connect_time\":\"2026-04-15 20:00:00\"}"),
            new Document("event_type", "TrafficAlert")
                .append("tower_id", "1").append("user_id", null)
                .append("timestamp", "2026-04-11 02:01:00")
                .append("details", "{\"expected_load_pct\":20,\"actual_load_pct\":65}")
        ));
        System.out.println("[MongoSeeder] eventlog seeded with 10 documents.");
    }

    private static void seedAnomalyLog() {
        MongoCollection<Document> col = MongoConn.getDatabase().getCollection("anomalylog");
        if (col.countDocuments() > 0) {
            System.out.println("[MongoSeeder] anomalylog already has data — skipping.");
            return;
        }
        col.insertMany(List.of(
            new Document("anomaly_type", "High Data Usage Spike")
                .append("risk_level", "High").append("tower_id", "1").append("user_id", "2")
                .append("anomaly_time", "2026-04-10 11:00:00").append("anomaly_flag", 1)
                .append("description", "User exceeded 1.5 GB within 2-hour window"),
            new Document("anomaly_type", "Signal Drop >30dB")
                .append("risk_level", "Critical").append("tower_id", "2").append("user_id", null)
                .append("anomaly_time", "2026-04-10 12:00:00").append("anomaly_flag", 1)
                .append("description", "Sudden signal degradation; hardware fault suspected"),
            new Document("anomaly_type", "Excessive Session Duration")
                .append("risk_level", "Medium").append("tower_id", "4").append("user_id", "7")
                .append("anomaly_time", "2026-04-10 13:30:00").append("anomaly_flag", 1)
                .append("description", "Session exceeds 2.5 hours at peak load hours"),
            new Document("anomaly_type", "Congestion Critical")
                .append("risk_level", "Critical").append("tower_id", "30").append("user_id", null)
                .append("anomaly_time", "2026-04-11 08:00:00").append("anomaly_flag", 1)
                .append("description", "Active users exceeded max_capacity 650; load at 97%"),
            new Document("anomaly_type", "Blocked User Connection")
                .append("risk_level", "High").append("tower_id", "3").append("user_id", "9")
                .append("anomaly_time", "2026-04-12 12:00:00").append("anomaly_flag", 1)
                .append("description", "Blocked user attempted to connect from Rawalpindi tower"),
            new Document("anomaly_type", "Tower Offline")
                .append("risk_level", "Critical").append("tower_id", "35").append("user_id", null)
                .append("anomaly_time", "2026-04-15 08:00:00").append("anomaly_flag", 1)
                .append("description", "Tower 35 Rawalpindi shows Offline; zero active users"),
            new Document("anomaly_type", "Degraded Network Mode")
                .append("risk_level", "High").append("tower_id", "22").append("user_id", null)
                .append("anomaly_time", "2026-04-15 10:00:00").append("anomaly_flag", 1)
                .append("description", "Tower under maintenance; net_mode degraded"),
            new Document("anomaly_type", "Congestion Critical")
                .append("risk_level", "Critical").append("tower_id", "4").append("user_id", null)
                .append("anomaly_time", "2026-04-15 20:30:00").append("anomaly_flag", 1)
                .append("description", "Lahore Metro tower 4: 598/600 users; 99.7% load"),
            new Document("anomaly_type", "Unusual Off-Peak Traffic")
                .append("risk_level", "Medium").append("tower_id", "1").append("user_id", null)
                .append("anomaly_time", "2026-04-11 02:00:00").append("anomaly_flag", 1)
                .append("description", "Tower load at 65% at 2 AM; possible bot traffic"),
            new Document("anomaly_type", "High Data Usage Spike")
                .append("risk_level", "High").append("tower_id", "18").append("user_id", "2")
                .append("anomaly_time", "2026-04-15 11:30:00").append("anomaly_flag", 1)
                .append("description", "Sara Khan: 1950 MB in 2.5 hrs; second occurrence this week")
        ));
        System.out.println("[MongoSeeder] anomalylog seeded with 10 documents.");
    }

    private static void seedCyberThreat() {
        MongoCollection<Document> col = MongoConn.getDatabase().getCollection("cyberthreat");
        if (col.countDocuments() > 0) {
            System.out.println("[MongoSeeder] cyberthreat already has data — skipping.");
            return;
        }
        col.insertMany(List.of(
            new Document("attack_type", "Volumetric DDoS")
                .append("severity_level", "Critical").append("status", "Mitigated")
                .append("affected_towers", 3).append("detected_at", "2026-03-05 00:00:00")
                .append("description", "200 Gbps attack on Islamabad towers; auto-blocked at perimeter firewall"),
            new Document("attack_type", "SMS Phishing / Spoofing")
                .append("severity_level", "High").append("status", "Mitigated")
                .append("affected_towers", 8).append("detected_at", "2026-04-05 00:00:00")
                .append("description", "12,000 fake OTP messages targeting Jazz and Zong users"),
            new Document("attack_type", "APT Intrusion")
                .append("severity_level", "Critical").append("status", "Investigating")
                .append("affected_towers", 12).append("detected_at", "2026-05-01 00:00:00")
                .append("description", "APT actor accessed NOC management plane; possibly state-sponsored"),
            new Document("attack_type", "Ransomware")
                .append("severity_level", "High").append("status", "Resolved")
                .append("affected_towers", 5).append("detected_at", "2026-03-20 00:00:00")
                .append("description", "Ransomware on OSS/BSS billing; backup restored within 6 hours")
        ));
        System.out.println("[MongoSeeder] cyberthreat seeded with 4 documents.");
    }
}