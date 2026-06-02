package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * TableBuilder.java
 * Utility that creates styled JTables for the SQL panel.
 * All tables share the Ufone green/white theme.
 */
public class TableBuilder {

    // ── Colors ────────────────────────────────────────────────────────────────
    public static final Color HEADER_BG   = new Color(0x36, 0x65, 0x00); // #366500
    public static final Color ROW_ALT     = new Color(0xF4, 0xFC, 0xE0); // #F4FCE0
    public static final Color ROW_NORMAL  = Color.WHITE;
    public static final Color SELECT_BG   = new Color(0xAB, 0xE3, 0x00); // #ABE300
    public static final Color TEXT_DARK   = new Color(0x2E, 0x2E, 0x2E); // #2E2E2E

    /**
     * Creates a styled, non-editable JTable with the given columns and data.
     */
    public static JTable build(String[] columns, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        styleTable(table);
        return table;
    }

    /**
     * Creates an empty table with given columns — rows added later.
     */
    public static JTable buildEmpty(String[] columns) {
        return build(columns, new Object[0][columns.length]);
    }

    /**
     * Replaces all rows in an existing table with new data.
     */
    public static void refreshData(JTable table, Object[][] newData) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // clear
        for (Object[] row : newData) {
            model.addRow(row);
        }
    }

    /** Applies Ufone theme styling to any JTable. */
    public static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.setShowGrid(true);
        table.setGridColor(new Color(0xCC, 0xCC, 0xCC));
        table.setSelectionBackground(SELECT_BG);
        table.setSelectionForeground(TEXT_DARK);
        table.setForeground(TEXT_DARK);
        table.setBackground(ROW_NORMAL);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean selected, boolean focused, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, selected, focused, row, col);
                if (!selected) {
                    c.setBackground(row % 2 == 0 ? ROW_NORMAL : ROW_ALT);
                }
                return c;
            }
        });

        // Style header
        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);
    }

    /**
     * Wraps a JTable in a styled JScrollPane.
     */
    public static JScrollPane scrollWrap(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0xAB, 0xE3, 0x00), 2));
        return sp;
    }
}
