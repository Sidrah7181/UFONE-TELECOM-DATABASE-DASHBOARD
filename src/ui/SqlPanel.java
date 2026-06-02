package ui;

import crud.sql.*;
import model.sql.*;
import util.TableBuilder;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Timestamp;
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
Object[][] data = new Object[rows.size()][5];
for (int i = 0; i < rows.size(); i++) {
User u = rows.get(i);
data[i] = new Object[]{
u.getUserId(), u.getFullName(), u.getRegDate(), u.isHighUsageFlag()? "Heavy" : "Normal", u.isBlocked()? "BLOCKED" : "Active"
};
}
TableBuilder.refreshData(dataTable, data);
updateColumns(new String[]{ "ID", "Full Name", "Reg Date", "Usage", "Status" });
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
Object[][] data = new Object[rows.size()][5];
for (int i = 0; i < rows.size(); i++) {
Tower t = rows.get(i);
data[i] = new Object[]{
t.getTowerId(), t.getTowerType(), t.getRegionId(), t.getMaxCapacity(), t.getTowerStatus()
};
}
TableBuilder.refreshData(dataTable, data);
updateColumns(new String[]{"ID", "Type", "Region", "Capacity", "Status"});
}

private void updateColumns(String[] cols) {
javax.swing.table.DefaultTableModel m = (javax.swing.table.DefaultTableModel) dataTable.getModel();
m.setColumnIdentifiers(cols);
}

private void showForm() {
formLayout.show(formPanel, (String) tableSelector.getSelectedItem());
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
rfName.getText().trim(), rfMode.getText().trim(), rfType.getText().trim()
);
}
// ───────────────── TOWER ─────────────────
case "tower" -> {
ok = towerCRUD.insert(
Integer.parseInt(tfRegId.getText().trim()),
tfType.getText().trim(),
tfDate.getText().trim(),
Integer.parseInt(tfCap.getText().trim()),
tfLoc.getText().trim(),
tfStatus.getText().trim()
);
}
// ───────────────── USER ─────────────────
case "user_account" -> {
String name = ufName.getText().trim();
String birth = ufBirth.getText().trim();
// validation
if (name.isEmpty()) {
JOptionPane.showMessageDialog(
this, "Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE
);
return;
}
// optional birth-date validation
if (!birth.isEmpty()) {
try {
java.sql.Date.valueOf(birth); // YYYY-MM-DD
} catch (IllegalArgumentException ex) {
JOptionPane.showMessageDialog(
this, "Birth date must be YYYY-MM-DD", "Date Format Error", JOptionPane.ERROR_MESSAGE
);
return;
}
}
System.out.println("[DEBUG USER INSERT]");
System.out.println("Name : " + name);
System.out.println("Birth : " + birth);
System.out.println("Heavy : " + ufHeavy.isSelected());
ok = userCRUD.insert(
name, birth, ufHeavy.isSelected()
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
case "user_account" -> ok = userCRUD.delete((int) dataTable.getValueAt(row, 0));
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
// ───────────────── REGION ─────────────────
case "region" -> {
int id = (int) dataTable.getValueAt(row, 0);
ok = regionCRUD.update(
id,
rfName.getText().trim(),
rfMode.getText().trim(),
rfType.getText().trim()
);
}
// ───────────────── TOWER ─────────────────
case "tower" -> {
int id = (int) dataTable.getValueAt(row, 0);
try {
ok = towerCRUD.update(
id,
Integer.parseInt(tfRegId.getText().trim()),
tfType.getText().trim(),
tfDate.getText().trim(),
Integer.parseInt(tfCap.getText().trim()),
tfLoc.getText().trim(),
tfStatus.getText().trim()
);
} catch (NumberFormatException ex) {
JOptionPane.showMessageDialog(this, "Region ID and Capacity must be numbers.");
return;
}
}
// ───────────────── USER (FIXED PROPERLY) ─────────────────
case "user_account" -> {
int id = (int) dataTable.getValueAt(row, 0);
String name = ufName.getText().trim();
String birth = ufBirth.getText().trim();
ok = userCRUD.update(
id,
name.isEmpty()? null : name,
ufHeavy.isSelected(),
false
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
private JTextField rfName, rfMode, rfType;
private JTextField tfRegId, tfType, tfDate, tfCap, tfLoc, tfStatus;
private JTextField ufName, ufBirth;
private JCheckBox ufHeavy;
private JTextField lfUser, lfTower, lfConn, lfDisc, lfSignal, lfData;

private JPanel buildRegionForm() {
JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
rfName = addField(p, "Name:");
rfMode = addField(p, "Mode:");
rfType = addField(p, "Type:");
return p;
}

private JPanel buildTowerForm() {
JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
tfRegId = addField(p, "Region ID:");
tfType = addField(p, "Type:");
tfDate = addField(p, "Date:");
tfCap = addField(p, "Capacity:");
tfLoc = addField(p, "Location:");
tfStatus = addField(p, "Status:");
return p;
}

private JPanel buildUserForm() {
JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
ufName = addField(p, "Name:");
ufBirth = addField(p, "Birth:");
ufHeavy = new JCheckBox("Heavy?");
p.add(ufHeavy);
return p;
}

private JPanel buildLogForm() {
JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
lfUser = addField(p, "User:");
lfTower = addField(p, "Tower:");
lfConn = addField(p, "Connect:");
lfDisc = addField(p, "Disconnect:");
lfSignal = addField(p, "Signal:");
lfData = addField(p, "Data:");
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