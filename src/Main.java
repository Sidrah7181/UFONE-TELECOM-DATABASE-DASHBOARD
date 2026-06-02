import db.MongoConn;
import db.PostgresConn;
import javax.swing.*;
import ui.SplashScreen;
/**
 * Main.java
 * Entry point for the UFONE Telecom Network Simulation Dashboard.
 */
public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {

            System.out.println("=== UFONE Telecom Simulation Dashboard ===");
            System.out.println("Starting application...");

            // ── Splash ────────────────────────────────────────────
            SplashScreen splash = new SplashScreen();
            splash.showSplash();

            // ── Seed MongoDB ONCE (safe: skips if data already exists) ──
            if (MongoConn.testConnection()) {
                //MongoSeeder.seed();   // ← ADD THIS. Remove after first successful run.
            } else {
                System.err.println("[Main] MongoDB connection failed — skipping seed.");
            }

            // ── Safe shutdown ─────────────────────────────────────
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Closing database connections...");
                try { PostgresConn.close(); } catch (Exception ignored) {}
                try { MongoConn.close(); }   catch (Exception ignored) {}
                System.out.println("Goodbye.");
            }));
        });
    }
}