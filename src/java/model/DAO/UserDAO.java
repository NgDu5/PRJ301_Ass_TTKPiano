package model.DAO;

import java.sql.*;
import model.DTO.User;

public class UserDAO {

    public User login(String userID, String password) {
        String sql = "SELECT * FROM Users WHERE userID = ? AND password = ? AND status = 1";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByID(String userID) {
        String sql = "SELECT * FROM Users WHERE userID = ?";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserID(rs.getString("userID"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("fullName"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getBoolean("status"));
        return u;
    }

    /**
     * Tìm user theo email - dùng cho Google Login (Function 8)
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ? AND status = 1";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tạo user mới - dùng cho auto-register khi Google Login (Function 8)
     */
    public boolean createUser(User u) {
        String sql = "INSERT INTO Users (userID, password, fullName, email, phone, role, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, 1)";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUserID());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPhone() != null ? u.getPhone() : "");
            ps.setString(6, u.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Copy method mapRow này nếu UserDAO của bạn chưa có
    // ---------------------------------------------------------------
//    private User mapRow(ResultSet rs) throws SQLException {
//        User u = new User();
//        u.setUserID(rs.getString("userID"));
//        u.setPassword(rs.getString("password"));
//        u.setFullName(rs.getString("fullName"));
//        u.setEmail(rs.getString("email"));
//        u.setPhone(rs.getString("phone"));
//        u.setRole(rs.getString("role"));
//        u.setStatus(rs.getBoolean("status"));
//        return u;
//    }
}
