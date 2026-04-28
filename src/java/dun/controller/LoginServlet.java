package dun.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import model.DAO.UserDAO;
import model.DTO.User;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // If already logged in, redirect to course list
        if (req.getSession().getAttribute("loggedUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/courses");
            return;
        }
        req.getRequestDispatcher("views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userID   = request.getParameter("userID");
        String password = request.getParameter("password");

        if (userID == null || password == null || userID.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMsg", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            request.getRequestDispatcher("views/login.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(userID.trim(), password.trim());

        if (user == null) {
            request.setAttribute("errorMsg", "Tên đăng nhập hoặc mật khẩu không đúng.");
            request.setAttribute("inputUserID", userID);
            request.getRequestDispatcher("views/login.jsp").forward(request, response);
        } else {
            HttpSession session = request.getSession(true);
            session.setAttribute("loggedUser", user);

            // Redirect target
            String redirect = request.getParameter("redirect");
            if (redirect != null && !redirect.isEmpty()) {
                response.sendRedirect(redirect);
            } else if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/courses");
            } else {
                response.sendRedirect(request.getContextPath() + "/courses");
            }
        }
    }
}
