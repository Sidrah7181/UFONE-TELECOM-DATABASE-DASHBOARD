package ui;

import db.MongoConn;
import db.PostgresConn;

import javax.swing.*;
import java.awt.*;

/**
 * StatusBar.java
 * Bottom bar showing PostgreSQL and MongoDB connection status + active mode.
 */
public class StatusBar extends JPanel {

    private final JLabel sqlLabel;
    private final JLabel mongoLabel;
    private final JLabel modeLabel;

    // Colors
    private static final Color BG       = new Color(0x2E, 0x2E, 0x2E);
    private static final Color OK_GREEN = new Color(0xAB, 0xE3, 0x00);
    private static final Color ERR_RED  = new Color(0xFF, 0x44, 0x44);
    private static final Color TXT      = Color.WHITE;

    public StatusBar() {
        setBackground(BG);
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        setPreferredSize(new Dimension(0, 32));

        sqlLabel   = makeLabel("PostgreSQL: Checking...", Color.YELLOW);
        mongoLabel = makeLabel("MongoDB: Checking...",    Color.YELLOW);
        modeLabel  = makeLabel("Mode: —",                TXT);

        add(sqlLabel);
        add(sep());
        add(mongoLabel);
        add(sep());
        add(modeLabel);

        // Test connections in background so UI doesn't freeze on startup
        new Thread(this::checkConnections, "StatusChecker").start();
    }

    private void checkConnections() {
        boolean sqlOk   = PostgresConn.testConnection();
        boolean mongoOk = MongoConn.testConnection();
        SwingUtilities.invokeLater(() -> {
            sqlLabel.setText("PostgreSQL " + (sqlOk ? "✔ Connected" : "✘ Failed"));
            sqlLabel.setForeground(sqlOk ? OK_GREEN : ERR_RED);

            mongoLabel.setText("MongoDB " + (mongoOk ? "✔ Connected" : "✘ Failed"));
            mongoLabel.setForeground(mongoOk ? OK_GREEN : ERR_RED);
        });
    }

    /** Call this whenever the active tab changes. */
    public void setMode(String mode) {
        modeLabel.setText("Mode: " + mode);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel makeLabel(String text, Color fg) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(fg);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private JLabel sep() {
        JLabel s = new JLabel("  |  ");
        s.setForeground(new Color(0x66, 0x66, 0x66));
        return s;
    }
}
