package crud.sql;

import db.PostgresConn;
import model.sql.ConnectionLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlConnectionLogCRUD {

    // ─────────────────────────────────────────────
    // CREATE (FIXED: Timestamp-safe)
    // ─────────────────────────────────────────────
   public boolean insert(int userId,
                      int towerId,
                      Timestamp connectTime,
                      Timestamp disconnectTime,
                      double signalStrength,
                      double dataUsedMb) {

    String sql =
            "INSERT INTO connection_log " +
            "(user_id, tower_id, connect_time, disconnect_time, signal_strength, data_used_mb) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = PostgresConn.getConnection().prepareStatement(sql)) {

        System.out.println("[DEBUG LOG INSERT STARTED]");

        ps.setInt(1, userId);
        ps.setInt(2, towerId);

        ps.setTimestamp(3, connectTime);

        if (disconnectTime != null) {
            ps.setTimestamp(4, disconnectTime);
        } else {
            ps.setNull(4, Types.TIMESTAMP);
        }

        ps.setDouble(5, signalStrength);
        ps.setDouble(6, dataUsedMb);

        int rows = ps.executeUpdate();

        System.out.println("[DEBUG LOG INSERT ROWS] " + rows);

        return rows > 0;

    } catch (SQLException e) {
        System.err.println("[ConnectionLogCRUD][INSERT] " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    // ─────────────────────────────────────────────
    // READ ALL
    // ─────────────────────────────────────────────
    public List<ConnectionLog> getAll() {

        List<ConnectionLog> list = new ArrayList<>();

        String sql =
                "SELECT log_id, user_id, tower_id, connect_time, disconnect_time, " +
                "signal_strength, data_used_mb, duration_mins " +
                "FROM connection_log";

        try (Statement st = PostgresConn.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ConnectionLogCRUD][GET ALL] " + e.getMessage());
        }

        return list;
    }

    // ─────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────
    public boolean delete(long logId) {

        String sql = "DELETE FROM connection_log WHERE log_id=?";

        try (PreparedStatement ps = PostgresConn.getConnection().prepareStatement(sql)) {

            ps.setLong(1, logId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ConnectionLogCRUD][DELETE] " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // UPDATE (optional but safe)
    // ─────────────────────────────────────────────
    public boolean update(long logId,
                          Timestamp disconnectTime,
                          double signalStrength,
                          double dataUsedMb) {

        String sql =
                "UPDATE connection_log " +
                "SET disconnect_time=?, signal_strength=?, data_used_mb=? " +
                "WHERE log_id=?";

        try (PreparedStatement ps = PostgresConn.getConnection().prepareStatement(sql)) {

            if (disconnectTime != null) {
                ps.setTimestamp(1, disconnectTime);
            } else {
                ps.setNull(1, Types.TIMESTAMP);
            }

            ps.setDouble(2, signalStrength);
            ps.setDouble(3, dataUsedMb);
            ps.setLong(4, logId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ConnectionLogCRUD][UPDATE] " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // MAPPER (MATCHES YOUR FINAL MODEL)
    // ─────────────────────────────────────────────
    private ConnectionLog mapRow(ResultSet rs) throws SQLException {

        return new ConnectionLog(
                rs.getLong("log_id"),
                rs.getInt("user_id"),
                rs.getInt("tower_id"),
                rs.getTimestamp("connect_time"),
                rs.getTimestamp("disconnect_time"),
                rs.getDouble("signal_strength"),
                rs.getDouble("data_used_mb"),
                rs.getInt("duration_mins")
        );
    }
}