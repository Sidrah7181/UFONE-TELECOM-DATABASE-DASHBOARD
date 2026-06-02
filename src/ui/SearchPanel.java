package ui;

import service.SearchService;
import service.SearchService.SearchResult;
import model.sql.*;
import model.mongo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * SearchPanel.java
 * Unified search across SQL + MongoDB — results combined IN MEMORY ONLY.
 * No database sync. Databases never communicate.
 *
 * SQL results shown with green left border.
 * Mongo results shown with orange left border.
 */
public class SearchPanel extends JPanel {

    private static final Color BG      = new Color(0xF4, 0xFC, 0xE0);
    private static final Color GREEN   = new Color(0xAB, 0xE3, 0x00);
    private static final Color ORANGE  = new Color(0xF7, 0x82, 0x00);
    private static final Color DARK    = new Color(0x2E, 0x2E, 0x2E);

    private final SearchService searchService = new SearchService();
    private JTextField searchBox;
    private JPanel resultsArea;

    public SearchPanel() {
        setBackground(BG);
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        build();
    }

    private void build() {
        // ── Search bar ────────────────────────────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setBackground(BG);

        searchBox = new JTextField(28);
        searchBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GREEN, 2),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JButton btnSearch = new JButton("SEARCH");
        btnSearch.setBackground(ORANGE);
        btnSearch.setForeground(Color.BLACK);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(100, 34));
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSearch.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        JLabel hint = new JLabel("Global search across SQL + MongoDB");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);

        searchBar.add(new JLabel("🔍"));
        searchBar.add(searchBox);
        searchBar.add(btnSearch);
        searchBar.add(hint);
        add(searchBar, BorderLayout.NORTH);

        // ── Results area ──────────────────────────────────────────────────────
        resultsArea = new JPanel();
        resultsArea.setLayout(new BoxLayout(resultsArea, BoxLayout.Y_AXIS));
        resultsArea.setBackground(BG);

        JScrollPane scroll = new JScrollPane(resultsArea);
        scroll.setBorder(BorderFactory.createLineBorder(GREEN, 2));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ── Wire up ───────────────────────────────────────────────────────────
        btnSearch.addActionListener(e -> runSearch());
        searchBox.addActionListener(e -> runSearch()); // Enter key
    }

    private void runSearch() {
        String kw = searchBox.getText().trim();
        if (kw.isBlank()) {
            showPlaceholder("Enter a keyword to search.");
            return;
        }

        resultsArea.removeAll();
        resultsArea.add(loadingLabel());
        resultsArea.revalidate();

        // Run in background to avoid UI freeze
        new SwingWorker<List<SearchResult>, Void>() {
            @Override protected List<SearchResult> doInBackground() {
                return searchService.search(kw);
            }
            @Override protected void done() {
                try {
                    List<SearchResult> results = get();
                    resultsArea.removeAll();

                    if (results.isEmpty()) {
                        showPlaceholder("No results found for: " + kw);
                        return;
                    }

                    // Summary header
                    long sqlCount   = results.stream().filter(r -> r.source.equals("SQL")).count();
                    long mongoCount = results.stream().filter(r -> r.source.equals("MONGO")).count();
                    resultsArea.add(summaryLabel(results.size(), sqlCount, mongoCount));

                    for (SearchResult r : results) {
                        resultsArea.add(makeResultCard(r));
                        resultsArea.add(Box.createVerticalStrut(6));
                    }
                    resultsArea.revalidate();
                    resultsArea.repaint();
                } catch (Exception ex) {
                    showPlaceholder("Search error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ── Build a result card ───────────────────────────────────────────────────
    private JPanel makeResultCard(SearchResult r) {
        Color borderColor = r.source.equals("SQL") ? GREEN : ORANGE;
        String sourceTag  = r.source.equals("SQL")
            ? "🟩 SQL — " + r.type
            : "🟧 MONGO — " + r.type;

        JPanel card = new JPanel(new BorderLayout(6, 4));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, borderColor),
            new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel tagLabel = new JLabel(sourceTag);
        tagLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tagLabel.setForeground(borderColor.darker());

        JTextArea detail = new JTextArea(formatResult(r));
        detail.setFont(new Font("Monospaced", Font.PLAIN, 11));
        detail.setEditable(false);
        detail.setBackground(Color.WHITE);
        detail.setForeground(DARK);
        detail.setBorder(null);

        card.add(tagLabel, BorderLayout.NORTH);
        card.add(detail,   BorderLayout.CENTER);
        return card;
    }

    /** Converts a SearchResult's data to a readable string. */
    private String formatResult(SearchResult r) {
        if (r.data instanceof User u)
            return "ID: " + u.getUserId() + " | Name: " + u.getFullName() +
                   " | Registered: " + u.getRegDate() + " | Status: " + (u.isBlocked() ? "BLOCKED" : "Active");
        if (r.data instanceof Tower t)
            return "Tower ID: " + t.getTowerId() + " | Type: " + t.getTowerType() +
                   " | Region ID: " + t.getRegionId() + " | Status: " + t.getTowerStatus();
        if (r.data instanceof Region reg)
            return "Region ID: " + reg.getRegionId() + " | Name: " + reg.getRegionName() +
                   " | Mode: " + reg.getNetworkMode();
        if (r.data instanceof EventLog e)
            return "Event: " + e.getEventType() + " | Tower: " + e.getTowerId() +
                   " | Time: " + e.getTimestamp() + "\n" + e.getDetails();
        if (r.data instanceof Anomaly a)
            return "Type: " + a.getAnomalyType() + " | Risk: " + a.getRiskLevel() +
                   " | Tower: " + a.getTowerId() + "\n" + a.getDescription();
        return r.data.toString();
    }

    private JLabel summaryLabel(int total, long sql, long mongo) {
        JLabel l = new JLabel(String.format(
            "  Found %d result(s)  —  🟩 SQL: %d   🟧 MongoDB: %d", total, sql, mongo));
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(DARK);
        l.setBorder(new EmptyBorder(4, 4, 8, 4));
        return l;
    }

    private JLabel loadingLabel() {
        JLabel l = new JLabel("  Searching both databases...");
        l.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        l.setForeground(Color.GRAY);
        return l;
    }

    private void showPlaceholder(String msg) {
        resultsArea.removeAll();
        JLabel l = new JLabel("  " + msg);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        l.setForeground(Color.GRAY);
        resultsArea.add(l);
        resultsArea.revalidate();
        resultsArea.repaint();
    }
}