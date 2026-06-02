package db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.MongoIterable;

/**
 * MongoConn.java
 * Singleton connection handler for MongoDB Atlas.
 */
public class MongoConn {

    private static MongoClient mongoClient = null;
    private static MongoDatabase database  = null;

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(DbConfig.MONGO_URI);
            database    = mongoClient.getDatabase(DbConfig.MONGO_DB);
        }
        return database;
    }

    /**
     * Tests connectivity with a real ping command.
     * Prints the actual error if it fails so you can diagnose it.
     */
    public static boolean testConnection() {
        try {
            // runCommand is a definitive connectivity test
            getDatabase().runCommand(new Document("ping", 1));
            System.out.println("[MongoConn] Connected to database: " + DbConfig.MONGO_DB);
            // Print collection names so you can confirm collections exist
            System.out.print("[MongoConn] Collections found: ");
            getDatabase().listCollectionNames().forEach(
                name -> System.out.print(name + " ")
            );
            System.out.println();
            return true;
        } catch (Exception e) {
            System.err.println("[MongoConn] Connection FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database    = null;
        }
    }
}