package dun.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.DAO.CategoryDAO;
import model.DAO.CourseDAO;
import model.DTO.Category;
import model.DTO.Course;

@WebServlet(name = "CourseListServlet", urlPatterns = {"/courses"})
public class CourseListServlet extends HttpServlet {

    private static final int PAGE_SIZE = 20;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String keyword    = req.getParameter("keyword");
        String catParam   = req.getParameter("categoryID");
        String pageParam  = req.getParameter("page");

        int categoryID = 0;
        try { if (catParam != null) categoryID = Integer.parseInt(catParam); } catch (NumberFormatException ignored) {}

        int page = 1;
        try { if (pageParam != null) page = Integer.parseInt(pageParam); } catch (NumberFormatException ignored) {}
        if (page < 1) page = 1;

        CourseDAO  courseDAO  = new CourseDAO();
        CategoryDAO catDAO    = new CategoryDAO();

        int total      = courseDAO.countActiveCourses(keyword, categoryID);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;
        if (page > totalPages) page = totalPages;

        List<Course>   courses    = courseDAO.getActiveCourses(keyword, categoryID, page, PAGE_SIZE);
        List<Category> categories = catDAO.getAllCategories();

        req.setAttribute("courses",    courses);
        req.setAttribute("categories", categories);
        req.setAttribute("keyword",    keyword);
        req.setAttribute("categoryID", categoryID);
        req.setAttribute("page",       page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("total",      total);

        req.getRequestDispatcher("views/courseList.jsp").forward(req, resp);
    }
}
