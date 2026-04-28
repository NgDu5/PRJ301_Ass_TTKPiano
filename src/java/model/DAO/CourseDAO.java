package model.DAO;

import java.sql.*;
import java.util.*;
import model.DTO.Course;

public class CourseDAO {

    // ----------------------------------------------------------------
    // Public: get active courses with optional search, paging
    // ----------------------------------------------------------------
    public List<Course> getActiveCourses(String keyword, int categoryID, int page, int pageSize) {
        List<Course> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, cat.categoryName FROM Courses c " +
            "LEFT JOIN Categories cat ON c.categoryID = cat.categoryID " +
            "WHERE c.status = 'active' AND c.quantity > 0 "
        );
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND c.courseName LIKE ? ");
        }
        if (categoryID > 0) {
            sql.append("AND c.categoryID = ? ");
        }
        sql.append("ORDER BY c.startDate ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword.trim() + "%");
            }
            if (categoryID > 0) {
                ps.setInt(idx++, categoryID);
            }
            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) { list.add(mapRow(rs)); }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countActiveCourses(String keyword, int categoryID) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM Courses WHERE status = 'active' AND quantity > 0 "
        );
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND courseName LIKE ? ");
        }
        if (categoryID > 0) {
            sql.append("AND categoryID = ? ");
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword.trim() + "%");
            }
            if (categoryID > 0) {
                ps.setInt(idx, categoryID);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ----------------------------------------------------------------
    // Admin: get all courses with optional filters, paging
    // ----------------------------------------------------------------
    public List<Course> getAllCoursesAdmin(String keyword, int categoryID, String status,
                                           int page, int pageSize) {
        List<Course> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, cat.categoryName FROM Courses c " +
            "LEFT JOIN Categories cat ON c.categoryID = cat.categoryID WHERE 1=1 "
        );
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND c.courseName LIKE ? ");
        }
        if (categoryID > 0) {
            sql.append("AND c.categoryID = ? ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND c.status = ? ");
        }
        sql.append("ORDER BY c.startDate ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword.trim() + "%");
            }
            if (categoryID > 0) {
                ps.setInt(idx++, categoryID);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(idx++, status);
            }
            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) { list.add(mapRow(rs)); }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAllCoursesAdmin(String keyword, int categoryID, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Courses WHERE 1=1 ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND courseName LIKE ? ");
        }
        if (categoryID > 0) {
            sql.append("AND categoryID = ? ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND status = ? ");
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(idx++, "%" + keyword.trim() + "%");
            }
            if (categoryID > 0) {
                ps.setInt(idx++, categoryID);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(idx, status);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Course getCourseByID(int courseID) {
        String sql = "SELECT c.*, cat.categoryName FROM Courses c " +
                     "LEFT JOIN Categories cat ON c.categoryID = cat.categoryID " +
                     "WHERE c.courseID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateCourse(Course c, String updaterID) {
        String sql = "UPDATE Courses SET courseName=?, image=?, description=?, tuitionFee=?, " +
                     "startDate=?, endDate=?, categoryID=?, status=?, quantity=?, " +
                     "lastUpdateDate=GETDATE(), lastUpdateUser=? WHERE courseID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseName());
            ps.setString(2, c.getImage());
            ps.setString(3, c.getDescription());
            ps.setDouble(4, c.getTuitionFee());
            ps.setDate(5, c.getStartDate());
            ps.setDate(6, c.getEndDate());
            ps.setInt(7, c.getCategoryID());
            ps.setString(8, c.getStatus());
            ps.setInt(9, c.getQuantity());
            ps.setString(10, updaterID);
            ps.setInt(11, c.getCourseID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int createCourse(Course c) {
        String sql = "INSERT INTO Courses (courseName, image, description, tuitionFee, startDate, endDate, " +
                     "categoryID, status, quantity, createDate) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 'active', ?, GETDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCourseName());
            ps.setString(2, c.getImage());
            ps.setString(3, c.getDescription());
            ps.setDouble(4, c.getTuitionFee());
            ps.setDate(5, c.getStartDate());
            ps.setDate(6, c.getEndDate());
            ps.setInt(7, c.getCategoryID());
            ps.setInt(8, c.getQuantity());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Decrease course quantity when order is placed.
     * Returns false if stock is insufficient.
     */
    public boolean decreaseQuantity(Connection conn, int courseID, int amount) throws SQLException {
        String check = "SELECT quantity FROM Courses WHERE courseID = ?";
        try (PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, courseID);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) < amount) return false;
        }
        String upd = "UPDATE Courses SET quantity = quantity - ? WHERE courseID = ?";
        try (PreparedStatement ps = conn.prepareStatement(upd)) {
            ps.setInt(1, amount);
            ps.setInt(2, courseID);
            ps.executeUpdate();
        }
        return true;
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseID(rs.getInt("courseID"));
        c.setCourseName(rs.getString("courseName"));
        c.setImage(rs.getString("image"));
        c.setDescription(rs.getString("description"));
        c.setTuitionFee(rs.getDouble("tuitionFee"));
        c.setStartDate(rs.getDate("startDate"));
        c.setEndDate(rs.getDate("endDate"));
        c.setCategoryID(rs.getInt("categoryID"));
        c.setCategoryName(rs.getString("categoryName"));
        c.setStatus(rs.getString("status"));
        c.setQuantity(rs.getInt("quantity"));
        c.setCreateDate(rs.getTimestamp("createDate"));
        c.setLastUpdateDate(rs.getTimestamp("lastUpdateDate"));
        c.setLastUpdateUser(rs.getString("lastUpdateUser"));
        return c;
    }
}
