package db;

/**
 * DbConfig.java
 * Central configuration for all database credentials.
 */
public class DbConfig {

    // ─── PostgreSQL (Supabase - POOLER) ──────────────────────────
    public static final String SQL_HOST = "aws-1-ap-southeast-1.pooler.supabase.com";
    public static final int    SQL_PORT = 5432;
    public static final String SQL_DB   = "postgres";
    public static final String SQL_USER = "postgres.npqtuhpfvqoklrscywoy";
    public static final String SQL_PASS = "Data123Base@";

    public static final String SQL_URL =
            "jdbc:postgresql://" + SQL_HOST + ":" + SQL_PORT + "/" + SQL_DB;

    // ─── MongoDB Atlas ────────────────────────────────────────────
    // FIX: MONGO_DB must match the actual database name in Atlas.
    // Your URI path says "Telecom_network_simulation" but Atlas dashboard
    // shows the real DB name. Set MONGO_DB to whichever appears in Atlas → Databases.
    // If your DB is called "TELECOMDATABASE", use that. If it is
    // "Telecom_network_simulation", use that. They MUST match.
    public static final String MONGO_URI =
            "mongodb+srv://aneeqanadeem18_db_user:HXfcswvZcC3X9fOs@firstcluster.bhw1pw8.mongodb.net/" +
            "?appName=TelecomCluster";   // ← removed DB name from URI; set it only in MONGO_DB
    public static final String MONGO_DB = "TELECOMDATABASE"; // ← confirm this matches Atlas
}
