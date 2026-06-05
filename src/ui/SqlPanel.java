package ui;

import crud.sql.*;
import model.sql.*;
import util.TableBuilder;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SqlPanel extends JPanel {
private static final Color BTN_INSERT = new Color(0x36, 0x65, 0x00);
private static final Color BTN_UPDATE = new Color(0xF7, 0x82, 0x00);
private static final Color BTN_DELETE = new Color(0xCC, 0x22, 0x22);
private static final Color BTN_VIEW = new Color(0x88, 0x88, 0x88);
private static final Color PANEL_BG = new Color(0xF4, 0xFC, 0xE0);
private static final Color ACCENT = new Color(0xAB, 0xE3, 0x00);

private final SqlRegionCRUD regionCRUD = new SqlRegionCRUD();
private final SqlTowerCRUD towerCRUD = new SqlTowerCRUD();
private final SqlUserCRUD userCRUD = new SqlUserCRUD();
private final SqlConnectionLogCRUD logCRUD = new SqlConnectionLogCRUD();
private List<Integer> userIds = new ArrayList<>(); // ADD THIS

private JComboBox<String> tableSelector;
private JTable dataTable;
private JPanel formPanel;
private CardLayout formLayout;

public SqlPanel() {
setBackground(PANEL_BG);
setLayout(new BorderLayout(8, 8));
setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
build();
}

private void build() {
JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
topBar.setBackground(PANEL_BG);
tableSelector = new JComboBox<>(new String[]{ "region", "tower", "user_account", "connection_log" });
JButton btnView = makeBtn("VIEW", BTN_VIEW);
JButton btnInsert = makeBtn("INSERT", BTN_INSERT);
JButton btnUpdate = makeBtn("UPDATE", BTN_UPDATE);
JButton btnDelete = makeBtn("DELETE", BTN_DELETE);

btnInsert.addActionListener(e -> handleInsert());
btnUpdate.addActionListener(e -> handleUpdate());
btnDelete.addActionListener(e -> handleDelete());

topBar.add(new JLabel("Table:"));
topBar.add(tableSelector);
topBar.add(btnView);
topBar.add(btnInsert);
topBar.add(btnUpdate);
topBar.add(btnDelete);
add(topBar, BorderLayout.NORTH);


dataTable = TableBuilder.buildEmpty(new String[]{"ID", "A", "B", "C", "D"});
add(TableBuilder.scrollWrap(dataTable), BorderLayout.CENTER);

dataTable.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        int row = dataTable.getSelectedRow();
        if (row >= 0) {
            String sel = (String) tableSelector.getSelectedItem();
            switch (sel) {
                case "region" -> fillRegionForm(row);
                case "tower" -> fillTowerForm(row);
                case "user_account" -> fillUserForm(row);
                case "connection_log" -> fillLogForm(row);
            }
        }
    }
});

// ─────────────────────────────────────────────
// FORM PANEL (RESTORED EXACTLY FROM YOUR CODE)
// ─────────────────────────────────────────────
formLayout = new CardLayout();
formPanel = new JPanel(formLayout);
formPanel.setBackground(PANEL_BG);
formPanel.setPreferredSize(new Dimension(0, 180));
formPanel.add(buildRegionForm(), "region");
formPanel.add(buildTowerForm(), "tower");
formPanel.add(buildUserForm(), "user_account");
formPanel.add(buildLogForm(), "connection_log");

TitledBorder tb = BorderFactory.createTitledBorder(
BorderFactory.createLineBorder(ACCENT, 2), " Form ");
formPanel.setBorder(tb);
add(formPanel, BorderLayout.SOUTH);

// FIX: always refresh table when switching
tableSelector.addActionListener(e -> {
showForm();
loadTable();
});

btnView.addActionListener(e -> loadTable());
btnDelete.addActionListener(e -> handleDelete());
loadTable();
}

// ───────────────────────── TABLE LOADING ─────────────────────────
private void loadTable() {
String sel = (String) tableSelector.getSelectedItem();
switch (sel) {
case "region" -> loadRegions();
case "tower" -> loadTowers();
case "user_account" -> loadUsers();
case "connection_log" -> loadLogs();
}
}

private void loadUsers() {
    List<User> rows = userCRUD.getAll();
    userIds.clear(); // ADD THIS
    Object[][] data = new Object[rows.size()][5];
    for (int i = 0; i < rows.size(); i++) {
        User u = rows.get(i);
        userIds.add(u.getUserId()); // ADD THIS
        data[i] = new Object[]{
            u.getFullName(),
            u.getDateBirth(),
            u.getRegDate(),
            u.isHighUsageFlag()? "Heavy" : "Normal",
            u.isBlocked()? "Yes" : "No"
        };
    }
    TableBuilder.refreshData(dataTable, data);
    updateColumns(new String[]{"Name", "Birth Date", "Reg Date", "Usage", "IsBlocked"});
}

private void loadLogs() {
List<ConnectionLog> rows = logCRUD.getAll();
Object[][] data = new Object[rows.size()][5];
for (int i = 0; i < rows.size(); i++) {
ConnectionLog l = rows.get(i);
data[i] = new Object[]{
l.getLogId(), l.getUserId(), l.getTowerId(), l.getDataUsedMb(), l.getDurationMins()
};
}
TableBuilder.refreshData(dataTable, data);
updateColumns(new String[]{"Log ID", "User", "Tower", "Data", "Duration"});
}

private void loadRegions() {
List<Region> rows = regionCRUD.getAll();
Object[][] data = new Object[rows.size()][5];
for (int i = 0; i < rows.size(); i++) {
Region r = rows.get(i);
data[i] = new Object[]{
r.getRegionId(), r.getRegionName(), r.getNetworkMode(), r.getRegionType(), r.getStatus()
};
}
TableBuilder.refreshData(dataTable, data);
updateColumns(new String[]{"ID", "Name", "Mode", "Type", "Status"});
}

private void loadTowers() {
    List<Tower> rows = towerCRUD.getAll();
    Object[][] data = new Object[rows.size()][7];

    for (int i = 0; i < rows.size(); i++) {
        Tower t = rows.get(i);
        data[i] = new Object[]{
                t.getTowerId(),
                t.getRegionId(), // was column 2
                t.getTowerType(), // was column 1 - swapped
                t.getInstallDate(),
                t.getMaxCapacity(),
                t.getTowerLoc(),
                t.getTowerStatus()
        };
    }

    dataTable.setModel(new javax.swing.table.DefaultTableModel(
            data,
            new String[]{"ID", "Region ID", "Type", "Install Date", "Capacity", "Location", "Status"}
    ));
}

private void updateColumns(String[] cols) {
javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) dataTable.getModel();
m.setColumnIdentifiers(cols);
}

private void showForm() {
    formLayout.show(formPanel, (String) tableSelector.getSelectedItem());
}

// ADD THESE 5 METHODS HERE
private void fillRegionForm(int row) {
    rfName.setText(str(dataTable.getValueAt(row, 1)));
    rfMode.setSelectedItem(str(dataTable.getValueAt(row, 2)));
    rfType.setSelectedItem(str(dataTable.getValueAt(row, 3)));
    rfStatus.setSelectedItem(str(dataTable.getValueAt(row, 4))); // ADD THIS
}

private void fillTowerForm(int row) {
    tfRegId.setText(str(dataTable.getValueAt(row, 1)));
    tfType.setSelectedItem(str(dataTable.getValueAt(row, 2)));
    tfDate.setText(str(dataTable.getValueAt(row, 3)));
    tfCap.setText(str(dataTable.getValueAt(row, 4)));
    tfLoc.setText(str(dataTable.getValueAt(row, 5)));
    tfStatus.setSelectedItem(str(dataTable.getValueAt(row, 6)));
}

private void fillUserForm(int row) {
    ufName.setText(str(dataTable.getValueAt(row, 0))); // was 1, now 0
    ufBirth.setText(str(dataTable.getValueAt(row, 1))); // was 2, now 1
    String usage = str(dataTable.getValueAt(row, 3)); // was 3, still 3
    ufHeavy.setSelected("Heavy".equals(usage));
    String blocked = str(dataTable.getValueAt(row, 4)); // ADD THIS
    ufBlocked.setSelected("Yes".equals(blocked)); // ADD THIS
}

private void fillLogForm(int row) {
    lfUser.setText(str(dataTable.getValueAt(row, 1)));
    lfTower.setText(str(dataTable.getValueAt(row, 2)));
    lfSignal.setText(str(dataTable.getValueAt(row, 3)));
    lfData.setText(str(dataTable.getValueAt(row, 4)));
}

private String str(Object o) {
    return o == null? "" : o.toString();
}

// ───────────────────────── INSERT / DELETE ─────────────────────────
private void handleInsert() {
String sel = (String) tableSelector.getSelectedItem();
boolean ok = false;
try {
switch (sel) {
// ───────────────── REGION ─────────────────
case "region" -> {
    ok = regionCRUD.insert(
        rfName.getText().trim(), 
        (String) rfMode.getSelectedItem(), 
        (String) rfType.getSelectedItem(),
        (String) rfStatus.getSelectedItem() // ADDED THIS
    );
}
// ───────────────── TOWER ─────────────────
case "tower" -> {
ok = towerCRUD.insert(
Integer.parseInt(tfRegId.getText().trim()),
(String) tfType.getSelectedItem(),
tfDate.getText().trim(),
Integer.parseInt(tfCap.getText().trim()),
tfLoc.getText().trim(),
(String) tfStatus.getSelectedItem()
);
}
// ───────────────── USER ─────────────────
case "user_account" -> {
    String name = ufName.getText().trim();
    String birth = ufBirth.getText().trim();
    if (name.isEmpty()) {
        JOptionPane.showMessageDialog(
            this, "Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE
        );
        return;
    }
    if (!birth.isEmpty()) {
        try {
            java.sql.Date.valueOf(birth);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this, "Birth date must be YYYY-MM-DD", "Date Format Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }
    }
    ok = userCRUD.insert(
        name,
        birth,
        ufHeavy.isSelected(),
        ufBlocked.isSelected() // CHANGED: was missing
    );
}
// ───────────────── CONNECTION LOG ─────────────────
case "connection_log" -> {
Timestamp connTime = Timestamp.valueOf(lfConn.getText().trim());
Timestamp discTime = null;
if (!lfDisc.getText().trim().isEmpty()) {
discTime = Timestamp.valueOf(lfDisc.getText().trim());
}
ok = logCRUD.insert(
Integer.parseInt(lfUser.getText().trim()),
Integer.parseInt(lfTower.getText().trim()),
connTime,
discTime,
Double.parseDouble(lfSignal.getText().trim()),
Double.parseDouble(lfData.getText().trim())
);
}
}
// ───────────────── RESULT ─────────────────
if (ok) {
loadTable();
JOptionPane.showMessageDialog(
this, "Insert successful!" );
} else {
JOptionPane.showMessageDialog(
this, "Insert failed!", "Database Error", JOptionPane.ERROR_MESSAGE );
}
} catch (Exception ex) {
ex.printStackTrace();
JOptionPane.showMessageDialog(
this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE );
}
}

private void handleDelete() {
int row = dataTable.getSelectedRow();
if (row < 0) return;
String sel = (String) tableSelector.getSelectedItem();
boolean ok = false;
switch (sel) {
case "region" -> ok = regionCRUD.delete((int) dataTable.getValueAt(row, 0));
case "tower" -> ok = towerCRUD.delete((int) dataTable.getValueAt(row, 0));
case "user_account" -> ok = userCRUD.delete(userIds.get(row)); // CHANGED
case "connection_log" -> ok = logCRUD.delete((long) dataTable.getValueAt(row, 0));
}
if (ok) {
loadTable();
JOptionPane.showMessageDialog(this, "Deleted successfully!");
} else {
JOptionPane.showMessageDialog(this, "Delete failed!", "Error", JOptionPane.ERROR_MESSAGE);
}
}

private void handleUpdate() {
String sel = (String) tableSelector.getSelectedItem();
int row = dataTable.getSelectedRow();
if (row < 0) {
JOptionPane.showMessageDialog(this, "Select a row first.");
return;
}
boolean ok = false;
try {
switch (sel) {

case "region" -> {
    int id = (int) dataTable.getValueAt(row, 0);
    ok = regionCRUD.update(
        id,
        rfName.getText().trim(),
        (String) rfMode.getSelectedItem(),
        (String) rfType.getSelectedItem(),
        (String) rfStatus.getSelectedItem() // ADDED THIS
    );
}
// ───────────────── TOWER ─────────────────
case "tower" -> {
int id = (int) dataTable.getValueAt(row, 0);
try {
ok = towerCRUD.update(
id,
Integer.parseInt(tfRegId.getText().trim()),
(String) tfType.getSelectedItem(),
tfDate.getText().trim(),
Integer.parseInt(tfCap.getText().trim()),
tfLoc.getText().trim(),
(String) tfStatus.getSelectedItem()
);
} catch (NumberFormatException ex) {
JOptionPane.showMessageDialog(this, "Region ID and Capacity must be numbers.");
return;
}
}
// ───────────────── USER (FIXED PROPERLY) ─────────────────
case "user_account" -> {
    int row1 = dataTable.getSelectedRow();
    int id = userIds.get(row1); // CHANGED: was reading from table column 0 which is name now

    String name = ufName.getText().trim();
    String birth = ufBirth.getText().trim();

    if (name.isEmpty()) {
        JOptionPane.showMessageDialog(
            this, "Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE
        );
        return;
    }
    if (!birth.isEmpty()) {
        try {
            java.sql.Date.valueOf(birth);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this, "Birth date must be YYYY-MM-DD", "Date Format Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }
    }

    ok = userCRUD.update(
        id,
        name,
        birth, // ADDED
        ufHeavy.isSelected(),
        ufBlocked.isSelected() // CHANGED: was hardcoded false
    );
}
// ───────────────── CONNECTION LOG ─────────────────
case "connection_log" -> {
JOptionPane.showMessageDialog(
this, "Logs are system-generated and cannot be updated.", "Info", JOptionPane.INFORMATION_MESSAGE );
return;
}
}
// ───────────────── RESULT ─────────────────
if (ok) {
loadTable();
JOptionPane.showMessageDialog(this, "Updated successfully!");
} else {
JOptionPane.showMessageDialog(this, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
}
} catch (Exception ex) {
ex.printStackTrace();
JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
}
}

// ───────────────────────── FORMS (UNCHANGED RESTORED) ─────────────────────────
private JTextField rfName;
private JComboBox<String> rfMode, rfType, rfStatus;

private JTextField tfRegId, tfDate, tfCap, tfLoc;
private JComboBox<String> tfType, tfStatus;
private JTextField ufName, ufBirth;
private JCheckBox ufHeavy, ufBlocked;
private JTextField lfUser, lfTower, lfConn, lfDisc, lfSignal, lfData;

private JPanel buildRegionForm() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
    
    p.add(new JLabel("Name:"));
    rfName = new JTextField(10);
    p.add(rfName);
    
    p.add(new JLabel("Mode:"));
    rfMode = new JComboBox<>(new String[]{"2G", "3G", "4G", "5G", "4G/5G"});
    p.add(rfMode);
    
    p.add(new JLabel("Type:"));
    rfType = new JComboBox<>(new String[]{"Urban", "Rural", "Sub-urban"});
    p.add(rfType);

    // ADD THESE 3 LINES
    p.add(new JLabel("Status:"));
    rfStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
    p.add(rfStatus);
    
    return p;
}

private JPanel buildTowerForm() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
    
    p.add(new JLabel("Region ID:"));
    tfRegId = new JTextField(6);
    p.add(tfRegId);
    
    p.add(new JLabel("Type:"));
    tfType = new JComboBox<>(new String[]{"2G", "3G", "4G", "5G"});
    p.add(tfType);
    
    p.add(new JLabel("Date:"));
    tfDate = new JTextField(10);
    p.add(tfDate);
    
    p.add(new JLabel("Capacity:"));
    tfCap = new JTextField(6);
    p.add(tfCap);
    
    p.add(new JLabel("Location:"));
    tfLoc = new JTextField(10);
    p.add(tfLoc);
    
    p.add(new JLabel("Status:"));
    tfStatus = new JComboBox<>(new String[]{"Active", "Inactive", "Maintenance"});
    p.add(tfStatus);
    
    return p;
}
private JPanel buildUserForm() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
    ufName = addField(p, "Name:");
    ufBirth = addField(p, "Birth (YYYY-MM-DD):");
    ufHeavy = new JCheckBox("Heavy?");
    p.add(ufHeavy);
    ufBlocked = new JCheckBox("Blocked?"); // ADD THESE 2 LINES
    p.add(ufBlocked);
    return p;
}

private JPanel buildLogForm() {
JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
lfUser = addField(p, "User ID:");
lfTower = addField(p, "Tower ID:");
lfConn = addField(p, "Connect Time YYYY-MM-DD HH-MM-SS:");
lfDisc = addField(p, "Disconnect Time YYYY-MM-DD HH-MM-SS:");
lfSignal = addField(p, "Signal Strength:");
lfData = addField(p, "Data Mb:");
return p;
}

private JTextField addField(JPanel p, String label) {
p.add(new JLabel(label));
JTextField tf = new JTextField(10);
p.add(tf);
return tf;
}

private JButton makeBtn(String text, Color bg) {
JButton b = new JButton(text);
b.setBackground(bg);
return b;
}
}