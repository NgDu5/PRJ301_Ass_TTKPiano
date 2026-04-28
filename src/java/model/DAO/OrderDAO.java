package model.DAO;

import java.sql.*;
import java.util.*;
import model.DTO.CartItem;
import model.DTO.Order;
import model.DTO.OrderDetail;

public class OrderDAO {

    /**
     * Cập nhật paymentStatus của order
     * @param orderID  mã đơn hàng
     * @param status   "paid" | "pending" | "failed" | "cancelled"
     */
    public boolean updatePaymentStatus(int orderID, String status) {
        String sql = "UPDATE Orders SET paymentStatus = ? WHERE orderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Create an order with its details in a single transaction.
     * Also decreases course quantities.
     * Returns generated orderID, or -1 on failure.
     */
    public int createOrder(Order order, List<CartItem> items) {
        String insertOrder =
            "INSERT INTO Orders (userID, guestName, guestEmail, guestPhone, orderDate, " +
            "paymentMethod, paymentStatus, totalAmount) " +
            "VALUES (?, ?, ?, ?, GETDATE(), ?, 'pending', ?)";
        String insertDetail =
            "INSERT INTO OrderDetails (orderID, courseID, quantity, tuitionFee) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Check stock and decrease quantities
                CourseDAO courseDAO = new CourseDAO();
                for (CartItem item : items) {
                    boolean ok = courseDAO.decreaseQuantity(conn, item.getCourseID(), item.getQuantity());
                    if (!ok) {
                        conn.rollback();
                        return -2; // out of stock
                    }
                }

                // 2. Insert order header
                int orderID;
                try (PreparedStatement ps = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, order.getUserID());
                    ps.setString(2, order.getGuestName());
                    ps.setString(3, order.getGuestEmail());
                    ps.setString(4, order.getGuestPhone());
                    ps.setString(5, order.getPaymentMethod());
                    ps.setDouble(6, order.getTotalAmount());
                    ps.executeUpdate();
                    ResultSet keys = ps.getGeneratedKeys();
                    if (!keys.next()) { conn.rollback(); return -1; }
                    orderID = keys.getInt(1);
                }

                // 3. Insert order details
                try (PreparedStatement ps = conn.prepareStatement(insertDetail)) {
                    for (CartItem item : items) {
                        ps.setInt(1, orderID);
                        ps.setInt(2, item.getCourseID());
                        ps.setInt(3, item.getQuantity());
                        ps.setDouble(4, item.getTuitionFee());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return orderID;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Order getOrderByID(int orderID) {
        String sql =
            "SELECT o.*, COALESCE(u.fullName, o.guestName) AS customerDisplayName " +
            "FROM Orders o LEFT JOIN Users u ON o.userID = u.userID " +
            "WHERE o.orderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Order order = mapOrder(rs);
                order.setOrderDetails(getDetailsByOrderID(orderID));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<OrderDetail> getDetailsByOrderID(int orderID) {
        List<OrderDetail> list = new ArrayList<>();
        String sql =
            "SELECT od.*, c.courseName FROM OrderDetails od " +
            "JOIN Courses c ON od.courseID = c.courseID WHERE od.orderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail d = new OrderDetail();
                d.setOrderDetailID(rs.getInt("orderDetailID"));
                d.setOrderID(rs.getInt("orderID"));
                d.setCourseID(rs.getInt("courseID"));
                d.setCourseName(rs.getString("courseName"));
                d.setQuantity(rs.getInt("quantity"));
                d.setTuitionFee(rs.getDouble("tuitionFee"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderID(rs.getInt("orderID"));
        o.setUserID(rs.getString("userID"));
        o.setGuestName(rs.getString("guestName"));
        o.setGuestEmail(rs.getString("guestEmail"));
        o.setGuestPhone(rs.getString("guestPhone"));
        o.setOrderDate(rs.getTimestamp("orderDate"));
        o.setPaymentMethod(rs.getString("paymentMethod"));
        o.setPaymentStatus(rs.getString("paymentStatus"));
        o.setTotalAmount(rs.getDouble("totalAmount"));
        o.setCustomerDisplayName(rs.getString("customerDisplayName"));
        return o;
    }
}
