package crud.sql;

import db.PostgresConn;
import model.sql.Region;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlRegionCRUD {

    // ─────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────
    public boolean insert(String regionName,
                          String networkMode,
                          String regionType,
                          String status) { // CHANGED: added status param

        String sql = """
            INSERT INTO region
            (region_name, network_mode, region_type, status)
            VALUES (?, CAST(? AS network_mode_type),?,?)
        """; // CHANGED: added status column and?

        try (Connection conn = PostgresConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, regionName);
            ps.setString(2, networkMode); // MUST match ENUM
            ps.setString(3, regionType);
            ps.setString(4, status); // ADDED

            int rows = ps.executeUpdate();
            System.out.println("[INSERT] Rows inserted: " + rows);

            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[INSERT ERROR]");
            e.printStackTrace();
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // READ ALL
    // ─────────────────────────────────────────────
    public List<Region> getAll() {

        List<Region> list = new ArrayList<>();

        String sql = """
            SELECT region_id, region_name, network_mode, region_type, status
            FROM region
            ORDER BY region_id
        """;

        try (Connection conn = PostgresConn.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Region(
                        rs.getInt("region_id"),
                        rs.getString("region_name"),
                        rs.getString("network_mode"),
                        rs.getString("region_type"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ─────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────
    public boolean update(int regionId,
                          String regionName,
                          String networkMode,
                          String regionType,
                          String status) { // CHANGED: added status param

        String sql = """
            UPDATE region
            SET
                region_name =?,
                network_mode = CAST(? AS network_mode_type),
                region_type =?,
                status =?
            WHERE region_id =?
        """; // CHANGED: added status =?

        try (Connection conn = PostgresConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, regionName);
            ps.setString(2, networkMode);
            ps.setString(3, regionType);
            ps.setString(4, status); // ADDED
            ps.setInt(5, regionId); // CHANGED: was 4, now 5

            int rows = ps.executeUpdate();
            System.out.println("[UPDATE] Rows updated: " + rows);

            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[UPDATE ERROR]");
            e.printStackTrace();
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────
    public boolean delete(int regionId) {

        String sql = "DELETE FROM region WHERE region_id =?";

        try (Connection conn = PostgresConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, regionId);

            int rows = ps.executeUpdate();
            System.out.println("[DELETE] Rows deleted: " + rows);

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Region> searchByName(String keyword) {

    List<Region> list = new ArrayList<>();

    String sql =
        "SELECT * FROM region " +
        "WHERE LOWER(TRIM(region_name)) LIKE LOWER(?) " +
        "OR LOWER(TRIM(region_type)) LIKE LOWER(?) " +
        "OR LOWER(TRIM(status)) LIKE LOWER(?)";

    try (PreparedStatement ps = PostgresConn.getConnection().prepareStatement(sql)) {

        String pattern = "%" + keyword.trim() + "%";

        ps.setString(1, pattern);
        ps.setString(2, pattern);
        ps.setString(3, pattern);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Region r = new Region();
            r.setRegionId(rs.getInt("region_id"));
            r.setRegionName(rs.getString("region_name"));
            r.setRegionType(rs.getString("region_type"));
            r.setNetworkMode(rs.getString("network_mode"));
            r.setStatus(rs.getString("status"));

            list.add(r);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
}