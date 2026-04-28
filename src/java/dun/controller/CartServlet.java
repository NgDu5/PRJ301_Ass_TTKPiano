package dun.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import model.DAO.CourseDAO;
import model.DAO.OrderDAO;
import model.DTO.CartItem;
import model.DTO.Course;
import model.DTO.Order;
import model.DTO.User;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    // ----------------------------------------------------------------
    // GET: display cart
    // ----------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Admin cannot use cart
        User user = getLoggedUser(req);
        if (user != null && user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/admin/courses");
            return;
        }

        Map<Integer, CartItem> cart = getCart(req);
        double total = cart.values().stream().mapToDouble(CartItem::getTotal).sum();

        req.setAttribute("cart",  cart);
        req.setAttribute("total", total);
        req.getRequestDispatcher("views/cart.jsp").forward(req, resp);
    }

    // ----------------------------------------------------------------
    // POST: add | remove | update | confirm
    // ----------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = getLoggedUser(req);
        if (user != null && user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/admin/courses");
            return;
        }

        String action = req.getParameter("action");

        switch (action == null ? "" : action) {
            case "add":     handleAdd(req, resp); break;
            case "remove":  handleRemove(req, resp); break;
            case "update":  handleUpdate(req, resp); break;
            case "confirm": handleConfirm(req, resp); break;
            default:        resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    // ----------------------------------------------------------------
    private void handleAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int courseID = Integer.parseInt(req.getParameter("courseID"));
            CourseDAO courseDAO = new CourseDAO();
            Course course = courseDAO.getCourseByID(courseID);
            if (course == null || !course.isActive() || course.getQuantity() <= 0) {
                resp.sendRedirect(req.getContextPath() + "/courses?error=Khoa hoc khong con cho.");
                return;
            }

            Map<Integer, CartItem> cart = getCart(req);
            if (cart.containsKey(courseID)) {
                cart.get(courseID).setQuantity(cart.get(courseID).getQuantity() + 1);
            } else {
                cart.put(courseID, new CartItem(courseID, course.getCourseName(),
                        course.getImage(), 1, course.getTuitionFee()));
            }
            saveCart(req, cart);
            resp.sendRedirect(req.getContextPath() + "/cart");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/courses");
        }
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int courseID = Integer.parseInt(req.getParameter("courseID"));
            Map<Integer, CartItem> cart = getCart(req);
            cart.remove(courseID);
            saveCart(req, cart);
        } catch (Exception ignored) {}
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int courseID = Integer.parseInt(req.getParameter("courseID"));
            int qty      = Integer.parseInt(req.getParameter("quantity"));
            Map<Integer, CartItem> cart = getCart(req);
            if (qty <= 0) {
                cart.remove(courseID);
            } else if (cart.containsKey(courseID)) {
                cart.get(courseID).setQuantity(qty);
            }
            saveCart(req, cart);
        } catch (Exception ignored) {}
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleConfirm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Map<Integer, CartItem> cart = getCart(req);
        if (cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=Gio hang trong.");
            return;
        }

        User loggedUser = getLoggedUser(req);

        // Guest: need name, email, phone
        String guestName  = req.getParameter("guestName");
        String guestEmail = req.getParameter("guestEmail");
        String guestPhone = req.getParameter("guestPhone");

        if (loggedUser == null) {
            // Validate guest info
            if (guestName == null || guestName.trim().isEmpty() ||
                guestEmail == null || guestEmail.trim().isEmpty() ||
                guestPhone == null || guestPhone.trim().isEmpty()) {

                // Show cart page with guest form
                double total = cart.values().stream().mapToDouble(CartItem::getTotal).sum();
                req.setAttribute("cart",          cart);
                req.setAttribute("total",         total);
                req.setAttribute("showGuestForm", true);
                req.setAttribute("errorMsg",      "Vui lòng điền đầy đủ thông tin.");
                req.getRequestDispatcher("views/cart.jsp").forward(req, resp);
                return;
            }
        }

        // Build Order
        Order order = new Order();
        if (loggedUser != null) {
            order.setUserID(loggedUser.getUserID());
        } else {
            order.setGuestName(guestName.trim());
            order.setGuestEmail(guestEmail.trim());
            order.setGuestPhone(guestPhone.trim());
        }
        order.setPaymentMethod("cash");
        order.setTotalAmount(cart.values().stream().mapToDouble(CartItem::getTotal).sum());

        List<CartItem> items = new ArrayList<>(cart.values());
        OrderDAO orderDAO = new OrderDAO();
        int orderID = orderDAO.createOrder(order, items);

        if (orderID == -2) {
            // Out of stock
            req.setAttribute("errorMsg", "Một số khóa học trong giỏ hàng đã hết chỗ. Vui lòng kiểm tra lại.");
            double total = cart.values().stream().mapToDouble(CartItem::getTotal).sum();
            req.setAttribute("cart",  cart);
            req.setAttribute("total", total);
            req.getRequestDispatcher("views/cart.jsp").forward(req, resp);
            return;
        }

        if (orderID > 0) {
            // Success: clear cart and show success
            saveCart(req, new LinkedHashMap<>());
            req.setAttribute("orderID", orderID);
            req.getRequestDispatcher("views/orderSuccess.jsp").forward(req, resp);
        } else {
            req.setAttribute("errorMsg", "Đặt hàng thất bại. Vui lòng thử lại");
            double total = cart.values().stream().mapToDouble(CartItem::getTotal).sum();
            req.setAttribute("cart",  cart);
            req.setAttribute("total", total);
            req.getRequestDispatcher("views/cart.jsp").forward(req, resp);
        }
    }

    // ----------------------------------------------------------------
    // Helper methods
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private Map<Integer, CartItem> getCart(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private void saveCart(HttpServletRequest req, Map<Integer, CartItem> cart) {
        req.getSession(true).setAttribute("cart", cart);
    }

    private User getLoggedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("loggedUser") : null;
    }
}
