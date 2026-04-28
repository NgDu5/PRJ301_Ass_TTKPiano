package dun.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import model.DAO.CategoryDAO;
import model.DAO.CourseDAO;
import model.DTO.Category;
import model.DTO.Course;
import model.DTO.User;

@WebServlet(name = "AdminCourseServlet", urlPatterns = {"/admin/courses"})
public class AdminCourseServlet extends HttpServlet {

    private static final int PAGE_SIZE = 20;

    // ----------------------------------------------------------------
    // Guard: only Admin can access
    // ----------------------------------------------------------------
    private boolean checkAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loggedUser") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" +
                    java.net.URLEncoder.encode(req.getRequestURI(), "UTF-8"));
            return false;
        }
        if (!user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/courses?error=Ban khong co quyen truy cap trang nay.");
            return false;
        }
        return true;
    }

    // ----------------------------------------------------------------
    // GET: list | edit form | create form
    // ----------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkAdmin(req, resp)) return;

        String action = req.getParameter("action");

        if ("edit".equals(action)) {
            showEditForm(req, resp);
        } else if ("create".equals(action)) {
            showCreateForm(req, resp);
        } else {
            showCourseList(req, resp);
        }
    }

    // ----------------------------------------------------------------
    // POST: update | create
    // ----------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!checkAdmin(req, resp)) return;

        String action = req.getParameter("action");

        if ("update".equals(action)) {
            handleUpdate(req, resp);
        } else if ("create".equals(action)) {
            handleCreate(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/courses");
        }
    }

    // ----------------------------------------------------------------
    private void showCourseList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword   = req.getParameter("keyword");
        String catParam  = req.getParameter("categoryID");
        String statusParam = req.getParameter("statusFilter");
        String pageParam = req.getParameter("page");

        int categoryID = 0;
        try { if (catParam != null) categoryID = Integer.parseInt(catParam); } catch (NumberFormatException ignored) {}
        int page = 1;
        try { if (pageParam != null) page = Integer.parseInt(pageParam); } catch (NumberFormatException ignored) {}
        if (page < 1) page = 1;

        CourseDAO   courseDAO = new CourseDAO();
        CategoryDAO catDAO    = new CategoryDAO();

        int total      = courseDAO.countAllCoursesAdmin(keyword, categoryID, statusParam);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;
        if (page > totalPages) page = totalPages;

        List<Course>   courses    = courseDAO.getAllCoursesAdmin(keyword, categoryID, statusParam, page, PAGE_SIZE);
        List<Category> categories = catDAO.getAllCategories();

        req.setAttribute("courses",      courses);
        req.setAttribute("categories",   categories);
        req.setAttribute("keyword",      keyword);
        req.setAttribute("categoryID",   categoryID);
        req.setAttribute("statusFilter", statusParam);
        req.setAttribute("page",         page);
        req.setAttribute("totalPages",   totalPages);
        req.setAttribute("total",        total);

        req.getRequestDispatcher("/views/admin/adminCourseList.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        try {
            int courseID = Integer.parseInt(idStr);
            CourseDAO   courseDAO = new CourseDAO();
            CategoryDAO catDAO    = new CategoryDAO();
            Course course = courseDAO.getCourseByID(courseID);
            if (course == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/courses?error=Khong tim thay khoa hoc.");
                return;
            }
            req.setAttribute("course",     course);
            req.setAttribute("categories", catDAO.getAllCategories());
            req.getRequestDispatcher("/views/admin/editCourse.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/admin/courses");
        }
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        CategoryDAO catDAO = new CategoryDAO();
        req.setAttribute("categories", catDAO.getAllCategories());
        req.getRequestDispatcher("/views/admin/createCourse.jsp").forward(req, resp);
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Course c = new Course();
            c.setCourseID(Integer.parseInt(req.getParameter("courseID")));
            c.setCourseName(req.getParameter("courseName"));
            c.setImage(req.getParameter("image"));
            c.setDescription(req.getParameter("description"));
            c.setTuitionFee(Double.parseDouble(req.getParameter("tuitionFee")));
            c.setStartDate(Date.valueOf(req.getParameter("startDate")));
            c.setEndDate(Date.valueOf(req.getParameter("endDate")));
            c.setCategoryID(Integer.parseInt(req.getParameter("categoryID")));
            c.setStatus(req.getParameter("status"));
            c.setQuantity(Integer.parseInt(req.getParameter("quantity")));

            User admin = (User) req.getSession().getAttribute("loggedUser");
            CourseDAO courseDAO = new CourseDAO();
            boolean ok = courseDAO.updateCourse(c, admin.getUserID());

            if (ok) {
                resp.sendRedirect(req.getContextPath() + "/admin/courses?success=Cap nhat khoa hoc thanh cong.");
            } else {
                req.setAttribute("errorMsg", "Cap nhat that bai.");
                showEditForm(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMsg", "Du lieu khong hop le: " + e.getMessage());
            showEditForm(req, resp);
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Course c = new Course();
            c.setCourseName(req.getParameter("courseName"));
            c.setImage(req.getParameter("image"));
            c.setDescription(req.getParameter("description"));
            c.setTuitionFee(Double.parseDouble(req.getParameter("tuitionFee")));
            c.setStartDate(Date.valueOf(req.getParameter("startDate")));
            c.setEndDate(Date.valueOf(req.getParameter("endDate")));
            c.setCategoryID(Integer.parseInt(req.getParameter("categoryID")));
            c.setQuantity(Integer.parseInt(req.getParameter("quantity")));

            CourseDAO courseDAO = new CourseDAO();
            int newID = courseDAO.createCourse(c);

            if (newID > 0) {
                resp.sendRedirect(req.getContextPath() + "/admin/courses?success=Tao khoa hoc thanh cong (ID: " + newID + ")");
            } else {
                req.setAttribute("errorMsg", "Tao khoa hoc that bai.");
                showCreateForm(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMsg", "Du lieu khong hop le: " + e.getMessage());
            showCreateForm(req, resp);
        }
    }
}
