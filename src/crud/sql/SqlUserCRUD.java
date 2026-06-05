package crud.sql;

import db.PostgresConn;
import model.sql.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlUserCRUD {

    // ─────────────────────────────────────────────
    // CREATE - ADDED isBlocked param
    // ─────────────────────────────────────────────
    public boolean insert(String fullName, String dateBirth, boolean highUsage, boolean isBlocked) {

        String sql = "INSERT INTO user_account " +
                     "(full_name, date_birth, high_usage_flag, is_blocked) " +
                     "VALUES (?,?,?,?)";

        try (PreparedStatement ps = PostgresConn.getConnection().prepareStatement(sql)) {

            ps.setString(1, fullName);

            if (dateBirth!= null &&!dateBirth.trim().isEmpty()) {
                ps.setDate(2, Date.valueOf(dateBirth.trim()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            ps.setInt(3, highUsage? 1 : 0);
            ps.setInt(4, isBlocked? 1 : 0); // CHANGED: was hardcoded 0

            int rows = ps.executeUpdate();
            System.out.println("[DEBUG] Rows affected: " + rows);
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[UserCRUD][INSERT] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // READ ALL
    // ─────────────────────────────────────────────
    public List<User> getAll() {

        List<User> list = new ArrayList<>();

        String sql = "SELECT user_id, full_name, date_birth, reg_date, " +
                     "high_usage_flag, is_blocked FROM user_account";

        try (Statement st = PostgresConn.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[UserCRUD][GET ALL] " + e.getMessage());
        }

        return list;
    }

    // ─────────────────────────────────────────────
    // SEARCH
    // ─────────────────────────────────────────────
    public List<User> searchByName(String keyword) {

    List<User> list = new ArrayList<>();

    String sql =
        "SELECT * FROM user_account " +
        "WHERE LOWER(full_name) LIKE ?";

    try (PreparedStatement ps = PostgresConn.getConnection().prepareStatement(sql)) {

        ps.setString(1, "%" + keyword.toLowerCase() + "%");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(mapRow(rs));
        }

    } catch (SQLException e) {
        System.err.println("[UserCRUD][SEARCH] " + e.getMessage());
        e.printStackTrace();
    }

    return list;
}
    // ─────────────────────────────────────────────
    // UPDATE - ADDED dateBirth param
    // ─────────────────────────────────────────────
    public boolean update(int userId, String fullName, String dateBirth, Boolean highUsage, Boolean isBlocked) {

        String sql = "UPDATE user_account SET " +
                     "full_name = COALESCE(?, full_name), " +
                     "date_birth = COALESCE(?, date_birth), " +
                     "high_usage_flag = COALESCE(?, high_usage_flag), " +
                     "is_blocked = COALESCE(?, is_blocked) " +
                     "WHERE user_id =?";

        try (PreparedStatement ps = PostgresConn.getConnection().prepareStatement(sql)) {

            // full name
            if (fullName!= null &&!fullName.trim().isEmpty())
                ps.setString(1, fullName);
            else
                ps.setNull(1, Types.VARCHAR);

            // birth date - ADDED
            if (dateBirth!= null &&!dateBirth.trim().isEmpty())
                ps.setDate(2, Date.valueOf(dateBirth.trim()));
            else
                ps.setNull(2, Types.DATE);

            // high usage
            if (highUsage!= null)
                ps.setInt(3, highUsage? 1 : 0);
            else
                ps.setNull(3, Types.INTEGER);

            // blocked
            if (isBlocked!= null)
                ps.setInt(4, isBlocked? 1 : 0);
            else
                ps.setNull(4, Types.INTEGER);

            ps.setInt(5, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserCRUD][UPDATE] " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────
    public boolean delete(int userId) {

        String sql = "DELETE FROM user_account WHERE user_id=?";

        try (PreparedStatement ps = PostgresConn.getConnection().prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserCRUD][DELETE] " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // MAPPER
    // ─────────────────────────────────────────────
    private User mapRow(ResultSet rs) throws SQLException {

        return new User(
                rs.getInt("user_id"),
                rs.getString("full_name"),
                rs.getDate("date_birth"),
                rs.getDate("reg_date"),
                rs.getInt("high_usage_flag") == 1,
                rs.getInt("is_blocked") == 1
        );
    }
}