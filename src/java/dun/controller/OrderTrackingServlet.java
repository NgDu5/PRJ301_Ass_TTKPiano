package dun.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import model.DAO.OrderDAO;
import model.DTO.Order;
import model.DTO.User;

@WebServlet(name = "OrderTrackingServlet", urlPatterns = {"/order-tracking"})
public class OrderTrackingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!requireLogin(req, resp)) return;
        req.getRequestDispatcher("views/orderTracking.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!requireLogin(req, resp)) return;

        String orderIDStr = req.getParameter("orderID");
        if (orderIDStr == null || orderIDStr.trim().isEmpty()) {
            req.setAttribute("errorMsg", "Vui lòng nhập mã đơn hàng.");
            req.getRequestDispatcher("views/orderTracking.jsp").forward(req, resp);
            return;
        }

        try {
            int orderID = Integer.parseInt(orderIDStr.trim());
            OrderDAO orderDAO = new OrderDAO();
            Order order = orderDAO.getOrderByID(orderID);

            if (order == null) {
                req.setAttribute("errorMsg", "Không tìm thấy đơn hàng với mã #" + orderID);
            } else {
                req.setAttribute("order", order);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("errorMsg", "Mã đơn hàng không hợp lệ.");
        }

        req.setAttribute("searchedOrderID", orderIDStr);
        req.getRequestDispatcher("views/orderTracking.jsp").forward(req, resp);
    }

    private boolean requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loggedUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" +
                    java.net.URLEncoder.encode(req.getContextPath() + "/order-tracking", "UTF-8"));
            return false;
        }
        return true;
    }
}
