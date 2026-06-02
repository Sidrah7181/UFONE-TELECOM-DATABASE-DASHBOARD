package crud.sql;

import db.PostgresConn;
import model.sql.Tower;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlTowerCRUD {
    public boolean insert(int regionId, String towerType, String installDate, int maxCapacity, String towerLoc, String towerStatus) {
        String sql = "INSERT INTO tower (region_id, tower_type, install_date, max_capacity, tower_loc, tower_status) VALUES (?,?,?,?,?,?)";
        Connection conn = null;
        try {
            conn = PostgresConn.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, regionId);
                ps.setObject(2, towerType, java.sql.Types.OTHER); // enum
                ps.setDate(3, Date.valueOf(installDate.trim()));
                ps.setInt(4, maxCapacity);
                ps.setString(5, towerLoc);
                ps.setObject(6, towerStatus, java.sql.Types.OTHER); // enum
                int rows = ps.executeUpdate();
                conn.commit();
                return rows > 0;
            }
        } catch (Exception e) {
            System.err.println("Tower INSERT error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rb) {
                    rb.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    public List<Tower> getAll() {
        List<Tower> list = new ArrayList<>();
        String sql = "SELECT * FROM tower ORDER BY tower_id";
        try (Connection c = PostgresConn.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Date d = rs.getDate("install_date");
                String date = (d == null) ? "" : d.toString();
                list.add(new Tower(
                        rs.getInt("tower_id"),
                        rs.getInt("region_id"),
                        rs.getString("tower_type"),
                        date,
                        rs.getInt("max_capacity"),
                        rs.getString("tower_loc"),
                        rs.getString("tower_status")
                ));
            }
        } catch (Exception e) {
            System.err.println("Tower GETALL error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean update(int towerId, int regionId, String towerType, String installDate, int maxCapacity, String towerLoc, String towerStatus) {
        String sql = "UPDATE tower SET region_id=?, tower_type=?, install_date=?, max_capacity=?, tower_loc=?, tower_status=? WHERE tower_id=?";
        Connection conn = null;
        try {
            conn = PostgresConn.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, regionId);
                ps.setObject(2, towerType, java.sql.Types.OTHER); // enum
                ps.setDate(3, Date.valueOf(installDate.trim()));
                ps.setInt(4, maxCapacity);
                ps.setString(5, towerLoc);
                ps.setObject(6, towerStatus, java.sql.Types.OTHER); // enum
                ps.setInt(7, towerId);
                int rows = ps.executeUpdate();
                conn.commit();
                return rows > 0;
            }
        } catch (Exception e) {
            System.err.println("Tower UPDATE error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rb) {
                    rb.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    public boolean delete(int towerId) {
        String sql = "DELETE FROM tower WHERE tower_id=?";
        Connection conn = null;
        try {
            conn = PostgresConn.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, towerId);
                int rows = ps.executeUpdate();
                conn.commit();
                return rows > 0;
            }
        } catch (Exception e) {
            System.err.println("Tower DELETE error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rb) {
                    rb.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    public List<Tower> searchByRegion(String keyword) {
    List<Tower> list = new ArrayList<>();
    String sql = """
        SELECT t.tower_id, t.region_id, t.tower_type, t.install_date,
               t.max_capacity, t.tower_loc, t.tower_status
        FROM tower t
        JOIN region r ON t.region_id = r.region_id
        WHERE r.region_name ILIKE?
        ORDER BY t.tower_id
    """;

    try (Connection conn = PostgresConn.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, "%" + keyword + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Date d = rs.getDate("install_date");
            String date = (d == null)? "" : d.toString();
            list.add(new Tower(
                    rs.getInt("tower_id"),
                    rs.getInt("region_id"),
                    rs.getString("tower_type"),
                    date,
                    rs.getInt("max_capacity"),
                    rs.getString("tower_loc"),
                    rs.getString("tower_status")
            ));
        }
    } catch (SQLException e) {
        System.err.println("[TowerCRUD][SEARCH BY REGION] " + e.getMessage());
        e.printStackTrace();
    }
    return list;
}
}