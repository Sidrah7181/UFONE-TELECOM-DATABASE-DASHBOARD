package ui;

import crud.mongo.MongoAnomalyCRUD;
import crud.mongo.MongoCyberThreatCRUD;
import crud.mongo.MongoEventCRUD;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.mongo.Anomaly;
import model.mongo.CyberThreat;
import model.mongo.EventLog;
import util.MongoValidator.ValidationException;
import util.SoundPlayer;

/**
 * MongoPanel.java — Fixed version.
 * Changes from broken version:
 *   1. makeCard() now correctly adds the card AND the spacer to cardsArea
 *   2. handleInsert/Update/Delete catch ValidationException (RuntimeException)
 *   3. updateFormLabels correctly shows/hides field 6 for all collections
 *   4. Search bar added without changing layout or colours
 */
public class MongoPanel extends JPanel {

    // ── UFONE THEME ───────────────────────────────────────────────
    private static final Color UFONE_GREEN  = new Color(0x00, 0xA6, 0x50);
    private static final Color UFONE_LIME   = new Color(0xB6, 0xFF, 0x2E);
    private static final Color UFONE_ORANGE = new Color(0xFF, 0x7A, 0x00);
    private static final Color BG_PANEL     = new Color(0xF6, 0xF8, 0xF3);
    private static final Color CARD_BG      = Color.WHITE;
    private static final Color TEXT_DARK    = new Color(0x2A, 0x2A, 0x2A);
    private static final Color BORDER_SOFT  = new Color(0xE6, 0xE6, 0xE6);

    // CRUD
    private final MongoEventCRUD       eventCRUD  = new MongoEventCRUD();
    private final MongoAnomalyCRUD     anomalyCRUD = new MongoAnomalyCRUD();
    private final MongoCyberThreatCRUD cyberCRUD  = new MongoCyberThreatCRUD();

    // UI
    private JComboBox<String> collectionSelector;
    private JTextField        searchField;
    private JPanel            cardsArea;

    private JTextField fField1, fField2, fField3, fField4, fField5, fField6;
    private JLabel     lField1, lField2, lField3, lField4, lField5, lField6;

    private String selectedDocId = null;

    public MongoPanel() {
        setBackground(BG_PANEL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        build();
    }

    // ─────────────────────────────────────────────────────────────
    private void build() {

        // ── TOP BAR ──────────────────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        topBar.setBackground(BG_PANEL);

        JLabel lbl = new JLabel("MongoDB Collections");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_DARK);

        collectionSelector = new JComboBox<>(
                new String[]{"eventlog", "anomalylog", "cyberthreat"});
        collectionSelector.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        searchField = new JTextField(14);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setToolTipText("Search keyword then press SEARCH");

        JButton btnView   = makeBtn("VIEW",   UFONE_GREEN);
        JButton btnSearch = makeBtn("SEARCH", new Color(0x00, 0x7A, 0xCC));
        JButton btnInsert = makeBtn("INSERT", UFONE_GREEN);
        JButton btnUpdate = makeBtn("UPDATE", UFONE_ORANGE);
        JButton btnDelete = makeBtn("DELETE", new Color(0xCC, 0x33, 0x33));

        topBar.add(lbl);
        topBar.add(collectionSelector);
        topBar.add(searchField);
        topBar.add(btnView);
        topBar.add(btnSearch);
        topBar.add(btnInsert);
        topBar.add(btnUpdate);
        topBar.add(btnDelete);

        add(topBar, BorderLayout.NORTH);

        // ── CARDS AREA ───────────────────────────────────────────
        cardsArea = new JPanel();
        cardsArea.setLayout(new BoxLayout(cardsArea, BoxLayout.Y_AXIS));
        cardsArea.setBackground(BG_PANEL);

        JScrollPane scroll = new JScrollPane(cardsArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_SOFT));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);

        // ── FORM ─────────────────────────────────────────────────
        JPanel formOuter = new JPanel(new BorderLayout());
        formOuter.setBackground(BG_PANEL);
        formOuter.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UFONE_ORANGE, 1), " Document Form "));

        JPanel form = new JPanel(new GridLayout(3, 4, 8, 6));
        form.setBackground(BG_PANEL);
        form.setBorder(new EmptyBorder(8, 10, 8, 10));

        lField1 = fl("Field 1"); fField1 = ft();
        lField2 = fl("Field 2"); fField2 = ft();
        lField3 = fl("Field 3"); fField3 = ft();
        lField4 = fl("Field 4"); fField4 = ft();
        lField5 = fl("Field 5"); fField5 = ft();
        lField6 = fl("Field 6"); fField6 = ft();

        form.add(lField1); form.add(fField1);
        form.add(lField2); form.add(fField2);
        form.add(lField3); form.add(fField3);
        form.add(lField4); form.add(fField4);
        form.add(lField5); form.add(fField5);
        form.add(lField6); form.add(fField6);

        formOuter.add(form, BorderLayout.CENTER);
        formOuter.setPreferredSize(new Dimension(0, 140));
        add(formOuter, BorderLayout.SOUTH);

        // ── WIRE ACTIONS ─────────────────────────────────────────
        btnView.addActionListener(e -> loadCollection());
        btnSearch.addActionListener(e -> handleSearch());
        collectionSelector.addActionListener(e -> {
            updateFormLabels();
            loadCollection();
        });
        btnInsert.addActionListener(e -> handleInsert());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());

        updateFormLabels();
        loadCollection();
    }

    // ─────────────────────────────────────────────────────────────
    // LOAD ALL
    // ─────────────────────────────────────────────────────────────
    private void loadCollection() {
        cardsArea.removeAll();
        String sel = (String) collectionSelector.getSelectedItem();

        switch (sel) {
            case "eventlog"   -> showEvents(eventCRUD.getAll());
            case "anomalylog" -> showAnomalies(anomalyCRUD.getAll());
            case "cyberthreat"-> showThreats(cyberCRUD.getAll());
        }

        if (cardsArea.getComponentCount() == 0)
            cardsArea.add(emptyCard("No documents found in: " + sel));

        cardsArea.revalidate();
        cardsArea.repaint();
    }

    // ─────────────────────────────────────────────────────────────
    // SEARCH  (Req #1)
    // ─────────────────────────────────────────────────────────────
    private void handleSearch() {
        String kw  = searchField.getText().trim();
        String sel = (String) collectionSelector.getSelectedItem();

        if (kw.isEmpty()) { loadCollection(); return; }

        cardsArea.removeAll();

        switch (sel) {
            case "eventlog"    -> showEvents(eventCRUD.searchByType(kw));
            case "anomalylog"  -> showAnomalies(anomalyCRUD.searchByRisk(kw));
            case "cyberthreat" -> {
                // filter client-side for cyber_threat (no search method defined)
                cyberCRUD.getAll().stream()
                    .filter(c -> c.getAttackType().toLowerCase().contains(kw.toLowerCase())
                              || c.getSeverityLevel().toLowerCase().contains(kw.toLowerCase())
                              || c.getStatus().toLowerCase().contains(kw.toLowerCase()))
                    .forEach(c -> addCard(c.toDisplayJson(), c.getId()));
            }
        }

        if (cardsArea.getComponentCount() == 0)
            cardsArea.add(emptyCard("No results for: " + kw));

        cardsArea.revalidate();
        cardsArea.repaint();
    }

    // ─────────────────────────────────────────────────────────────
    // INSERT  (Req #2, #3, #6)
    // ─────────────────────────────────────────────────────────────
    private void handleInsert() {
        String sel = (String) collectionSelector.getSelectedItem();
        try {
            boolean ok = false;
            switch (sel) {
                case "anomalylog" -> ok = anomalyCRUD.insert(
                        fField1.getText().trim(), fField2.getText().trim(),
                        fField3.getText().trim(), fField4.getText().trim(),
                        fField5.getText().trim(), fField6.getText().trim());

                case "cyberthreat" -> {
                    int towers;
                    try { towers = Integer.parseInt(fField4.getText().trim()); }
                    catch (NumberFormatException ex) {
                        throw new ValidationException("Affected Towers must be a whole number.");
                    }
                    ok = cyberCRUD.insert(
                        fField1.getText().trim(), fField2.getText().trim(),
                        fField3.getText().trim(), towers,
                        fField6.getText().trim(), fField5.getText().trim());
                }

                case "eventlog" -> ok = eventCRUD.insert(
                        fField1.getText().trim(), fField2.getText().trim(),
                        fField3.getText().trim(), fField4.getText().trim(),
                        fField5.getText().trim());
            }

            if (ok) {
                SoundPlayer.play("assets/ufone_intro.wav");
                JOptionPane.showMessageDialog(this,
                    "Document inserted into: " + sel,
                    "Insert Successful", JOptionPane.INFORMATION_MESSAGE);
                loadCollection();
                clearForm();
            }

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                ve.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Insert failed:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE  (Req #2, #3)
    // ─────────────────────────────────────────────────────────────
    private void handleUpdate() {
        if (selectedDocId == null) {
            JOptionPane.showMessageDialog(this,
                "Click a document card first to select it.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sel = (String) collectionSelector.getSelectedItem();
        try {
            boolean ok = false;
            switch (sel) {
                case "anomalylog"  -> ok = anomalyCRUD.update(
                        selectedDocId, fField2.getText().trim(), fField6.getText().trim());
                case "cyberthreat" -> ok = cyberCRUD.update(
                        selectedDocId, fField3.getText().trim(), fField6.getText().trim());
                case "eventlog"    -> ok = eventCRUD.update(
                        selectedDocId, fField5.getText().trim(), fField1.getText().trim());
            }
            if (ok) {
                JOptionPane.showMessageDialog(this,
                    "✔  Document updated successfully.",
                    "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                loadCollection();
                clearForm();
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                ve.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Update failed:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE  (Req #2, #3)
    // ─────────────────────────────────────────────────────────────
    private void handleDelete() {
        if (selectedDocId == null) {
            JOptionPane.showMessageDialog(this,
                "Click a document card first to select it.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this document?\nID: " + selectedDocId,
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        String sel = (String) collectionSelector.getSelectedItem();
        try {
            boolean ok = false;
            switch (sel) {
                case "anomalylog"  -> ok = anomalyCRUD.delete(selectedDocId);
                case "cyberthreat" -> ok = cyberCRUD.delete(selectedDocId);
                case "eventlog"    -> ok = eventCRUD.delete(selectedDocId);
            }
            if (ok) {
                JOptionPane.showMessageDialog(this,
                    "✔  Document deleted successfully.",
                    "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
                loadCollection();
                clearForm();
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                ve.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Delete failed:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // FORM LABELS PER COLLECTION
    // ─────────────────────────────────────────────────────────────
    private void updateFormLabels() {
        String sel = (String) collectionSelector.getSelectedItem();
        clearForm();
        selectedDocId = null;

        // Always make field 6 visible first, then hide only for event_log
        lField6.setVisible(true);
        fField6.setVisible(true);

        switch (sel) {
            case "anomalylog" -> {
                lField1.setText("Anomaly Type");
                lField2.setText("Risk Level (Low/Medium/High/Critical)");
                lField3.setText("Tower ID");
                lField4.setText("User ID (optional)");
                lField5.setText("Time  yyyy-MM-dd HH:mm:ss");
                lField6.setText("Description");
            }
            case "cyberthreat" -> {
                lField1.setText("Attack Type");
                lField2.setText("Severity (Low/Medium/High/Critical)");
                lField3.setText("Status (Detected/Investigating/Mitigated/Resolved)");
                lField4.setText("Affected Towers (number)");
                lField5.setText("Detected At  yyyy-MM-dd HH:mm:ss");
                lField6.setText("Description");
            }
            case "eventlog" -> {
                lField1.setText("Event Type");
                lField2.setText("Tower ID");
                lField3.setText("User ID (optional)");
                lField4.setText("Timestamp  yyyy-MM-dd HH:mm:ss");
                lField5.setText("Details");
                lField6.setText("");
                lField6.setVisible(false);
                fField6.setVisible(false);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // CARD HELPERS — BUG FIX: card is now added inside this method
    // ─────────────────────────────────────────────────────────────
    private void showEvents(List<EventLog> items) {
        items.forEach(e -> addCard(e.toDisplayJson(), e.getId()));
    }

    private void showAnomalies(List<Anomaly> items) {
        items.forEach(a -> addCard(a.toDisplayJson(), a.getId()));
    }

    private void showThreats(List<CyberThreat> items) {
        items.forEach(c -> addCard(c.toDisplayJson(), c.getId()));
    }

    /**
     * Creates a card, adds it to cardsArea, and wires the click-to-select.
     * The old makeCard() returned the card without adding it — this fixes that.
     */
    private void addCard(String json, String docId) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, UFONE_LIME),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JTextArea ta = new JTextArea(json);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setForeground(TEXT_DARK);
        ta.setBackground(CARD_BG);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        card.add(ta, BorderLayout.CENTER);

        // Highlight on selection
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedDocId = docId;
                fField1.setText(docId);
                // Visual feedback — brief highlight
                card.setBackground(new Color(0xE8, 0xF8, 0xE8));
                ta.setBackground(new Color(0xE8, 0xF8, 0xE8));
                Timer t = new Timer(600, ev -> {
                    card.setBackground(CARD_BG);
                    ta.setBackground(CARD_BG);
                });
                t.setRepeats(false);
                t.start();
            }
        });

        cardsArea.add(Box.createVerticalStrut(8));
        cardsArea.add(card);   // ← THE FIX: was missing in original
    }

    private JPanel emptyCard(String msg) {
        JPanel p = new JPanel();
        p.setBackground(BG_PANEL);
        JLabel l = new JLabel(msg);
        l.setForeground(Color.GRAY);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        p.add(l);
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    // UI HELPERS (unchanged styling)
    // ─────────────────────────────────────────────────────────────
    private JLabel fl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT_DARK);
        return l;
    }

    private JTextField ft() {
        JTextField t = new JTextField();
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return t;
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hover = bg.brighter();
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private void clearForm() {
        fField1.setText(""); fField2.setText(""); fField3.setText("");
        fField4.setText(""); fField5.setText(""); fField6.setText("");
        selectedDocId = null;
    }
}