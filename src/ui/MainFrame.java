package ui;

import util.SoundPlayer;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame.java
 * UFONE Telecom Dashboard - upgraded UI version
 */
public class MainFrame extends JFrame {

    // ── Colors (Ufone-inspired palette) ─────────────────────────────
    private static final Color HEADER_BG_1 = new Color(0x1F5E00);
    private static final Color HEADER_BG_2 = new Color(0x2F7A00);

    private static final Color ACCENT_ORANGE = new Color(0xF78200);
    private static final Color ACCENT_GREEN  = new Color(0xABE300);

    // Panels
    private final SqlPanel sqlPanel = new SqlPanel();
    private final MongoPanel mongoPanel = new MongoPanel();
    private final SearchPanel searchPanel = new SearchPanel();
    private final StatusBar statusBar = new StatusBar();

    private JPanel mainArea;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("UFONE — Telecom Network Simulation Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        buildUI();
        setVisible(true);

       
    }

    // ── UI BUILD ─────────────────────────────────────────────────────
    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(0xF7, 0x82, 0x00));
getContentPane().setBackground(new Color(0xF7, 0x82, 0x00));

        add(buildHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainArea = new JPanel(cardLayout);

        mainArea.add(sqlPanel, "SQL");
        mainArea.add(mongoPanel, "MONGO");
        mainArea.add(searchPanel, "SEARCH");
        

        add(mainArea, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        showPanel("SQL");
    }

    // ── HEADER (UPGRADED UI) ─────────────────────────────────────────
    private JPanel buildHeader() {

        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gp = new GradientPaint(
                        0, 0, HEADER_BG_1,
                        getWidth(), getHeight(), HEADER_BG_2
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        header.setPreferredSize(new Dimension(0, 85));

        // ── LEFT BRAND AREA ─────────────────────────────
        JPanel titleArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        titleArea.setOpaque(false);

        JLabel logoLabel = loadLogo();

        JPanel textBlock = new JPanel(new GridLayout(2, 1));
        textBlock.setOpaque(false);

        JLabel title = new JLabel("UFONE NETWORK CONTROL");
        title.setForeground(new Color(0xF7, 0x82, 0x00));
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.ORANGE);

        JLabel subtitle = new JLabel("Simulation • Monitoring • Anomaly Detection");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(ACCENT_GREEN);

        textBlock.add(title);
        textBlock.add(subtitle);

        JPanel brandBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        brandBox.setOpaque(false);
        brandBox.add(logoLabel);
        brandBox.add(textBlock);

        titleArea.add(brandBox);
        header.add(titleArea, BorderLayout.WEST);

        // ── RIGHT TAB AREA ─────────────────────────────
        JPanel tabArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 22));
        tabArea.setOpaque(false);

        JButton btnSql = makeTabBtn("SQL CORE", new Color(0xE6F4D7), new Color(0xD4F0B0), Color.BLACK);
        JButton btnMongo = makeTabBtn("MONGO EVENTS", Color.BLACK, new Color(255, 170, 80), Color.BLACK);
        JButton btnSearch = makeTabBtn("SEARCH", Color.BLACK, new Color(230, 230, 230), Color.BLACK);

        tabArea.add(btnSql);
        tabArea.add(btnMongo);
        tabArea.add(btnSearch);

        header.add(tabArea, BorderLayout.EAST);

        // ── ACTIONS ─────────────────────────────
        btnSql.addActionListener(e -> { showPanel("SQL");});
        btnMongo.addActionListener(e -> { showPanel("MONGO"); });
        btnSearch.addActionListener(e -> { showPanel("SEARCH"); });

        return header;
    }

    // ── SWITCH PANELS ────────────────────────────────────────────────
    private void showPanel(String name) {
        cardLayout.show(mainArea, name);
        statusBar.setMode(name);
    }

    // ── BUTTON FACTORY ───────────────────────────────────────────────
    private JButton makeTabBtn(String text, Color bg, Color hover, Color fg) {

        JButton btn = new JButton(text);

        btn.setBackground(bg);
        btn.setForeground(fg);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hover);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    // ── LOGO LOADER ──────────────────────────────────────────────────
    private JLabel loadLogo() {
        try {
            java.net.URL url = getClass().getResource("/assets/ufone_logo.png");

            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image scaled = icon.getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH);
                return new JLabel(new ImageIcon(scaled));
            }

        } catch (Exception ignored) {}

        // fallback logo
        JLabel fallback = new JLabel("U");
        fallback.setFont(new Font("Segoe UI", Font.BOLD, 28));
        fallback.setForeground(ACCENT_GREEN);
        fallback.setPreferredSize(new Dimension(50, 50));
        fallback.setOpaque(true);
        fallback.setBackground(new Color(0x1F5E00));
        fallback.setHorizontalAlignment(SwingConstants.CENTER);
        fallback.setBorder(BorderFactory.createLineBorder(ACCENT_ORANGE, 2));

        return fallback;
    }
}