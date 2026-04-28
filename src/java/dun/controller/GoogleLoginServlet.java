package dun.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import model.DAO.UserDAO;
import model.DTO.User;

/**
 * Function 8: Google OAuth2 Login
 *
 * Flow: 1. User clicks "Login with Google" → GET /google-login?action=redirect
 * 2. Servlet redirects to Google OAuth consent screen 3. Google redirects back
 * → GET /google-login?action=callback&code=xxx 4. Servlet exchanges code for
 * token, gets user info, creates/finds user in DB 5. Sets session, redirects to
 * /courses
 *
 * ⚠️ SETUP: 1. Go to https://console.cloud.google.com/ 2. Create a project →
 * Credentials → Create OAuth2 Client ID (Web Application) 3. Add Authorized
 * redirect URI: http://localhost:8080/TTKPiano/google-login?action=callback 4.
 * Fill in CLIENT_ID and CLIENT_SECRET below
 */
@WebServlet(name = "GoogleLoginServlet", urlPatterns = {"/google-login"})
public class GoogleLoginServlet extends HttpServlet {

    // ---------------------------------------------------------------
    // TODO: Replace with your Google OAuth2 credentials
    // ---------------------------------------------------------------
 
    // tự tạo id, secret của bạn copy link dán vào bên dươi
    private static final String CLIENT_ID = ""; 
    private static final String CLIENT_SECRET = ""; 


    private static final String REDIRECT_URI
            = "";

    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USER_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

    // ---------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if ("redirect".equals(action)) {
            handleRedirect(req, resp);
        } else if ("callback".equals(action)) {
            handleCallback(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    // ---------------------------------------------------------------
    // Step 1: Build Google OAuth URL and redirect
    // ---------------------------------------------------------------
    private void handleRedirect(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Generate state token to prevent CSRF
        String state = java.util.UUID.randomUUID().toString();
        req.getSession(true).setAttribute("oauthState", state);

        String authUrl = GOOGLE_AUTH_URL
                + "?client_id=" + urlEncode(CLIENT_ID)
                + "&redirect_uri=" + urlEncode(REDIRECT_URI)
                + "&response_type=code"
                + "&scope=" + urlEncode("openid email profile")
                + "&state=" + urlEncode(state)
                + "&access_type=online";

        resp.sendRedirect(authUrl);
    }

    // ---------------------------------------------------------------
    // Step 2: Google returns with code → exchange for token → get user info
    // ---------------------------------------------------------------
    private void handleCallback(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String code = req.getParameter("code");
        String state = req.getParameter("state");
        String error = req.getParameter("error");

        // User denied
        if (error != null || code == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=google_denied");
            return;
        }

        // Verify state
        HttpSession session = req.getSession(false);
        String savedState = (session != null) ? (String) session.getAttribute("oauthState") : null;
        if (savedState == null || !savedState.equals(state)) {
            req.setAttribute("errorMsg", "Yêu cầu không hợp lệ (state mismatch).");
            req.getRequestDispatcher("views/login.jsp").forward(req, resp);
            return;
        }
        session.removeAttribute("oauthState");

        // Exchange code for access token
        String tokenJson = exchangeCodeForToken(code);
        if (tokenJson == null) {
            req.setAttribute("errorMsg", "Không thể lấy token từ Google.");
            req.getRequestDispatcher("views/login.jsp").forward(req, resp);
            return;
        }

        String accessToken = extractJson(tokenJson, "access_token");
        if (accessToken == null) {
            req.setAttribute("errorMsg", "Token Google đã hết hạn. Sẽ cập nhập sau!!");
            req.getRequestDispatcher("views/login.jsp").forward(req, resp);
            return;
        }

        // Get user info from Google
        String userJson = getUserInfo(accessToken);
        if (userJson == null) {
            req.setAttribute("errorMsg", "Không thể lấy thông tin người dùng từ Google.");
            req.getRequestDispatcher("views/login.jsp").forward(req, resp);
            return;
        }

        String email = extractJson(userJson, "email");
        String name = extractJson(userJson, "name");
        String googleId = extractJson(userJson, "sub");  // Google user ID

        if (email == null) {
            req.setAttribute("errorMsg", "Không lấy được email từ Google.");
            req.getRequestDispatcher("views/login.jsp").forward(req, resp);
            return;
        }

        // Find or create user in DB
        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByEmail(email);

        if (user == null) {
            // Auto-register new user
            user = new User();
            // userID = "google_" + first 12 chars of googleId
            String uid = "google_" + (googleId != null ? googleId.substring(0, Math.min(12, googleId.length())) : email.hashCode());
            user.setUserID(uid);
            user.setFullName(name != null ? name : email);
            user.setEmail(email);
            user.setPassword("GOOGLE_AUTH"); // not used for login
            user.setRole("customer");
            user.setStatus(true);
            userDAO.createUser(user);
            // Re-fetch to ensure all fields populated
            user = userDAO.findByEmail(email);
        }

        if (user == null) {
            req.setAttribute("errorMsg", "Lỗi tạo tài khoản Google.");
            req.getRequestDispatcher("views/login.jsp").forward(req, resp);
            return;
        }

        // Login success
        session = req.getSession(true);
        session.setAttribute("loggedUser", user);

        if (user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/admin/courses");
        } else {
            resp.sendRedirect(req.getContextPath() + "/courses");
        }
    }

    // ---------------------------------------------------------------
    // OAuth2 REST helpers
    // ---------------------------------------------------------------
    private String exchangeCodeForToken(String code) {
        try {
            URL url = new URL(GOOGLE_TOKEN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String body = "code=" + urlEncode(code)
                    + "&client_id=" + urlEncode(CLIENT_ID)
                    + "&client_secret=" + urlEncode(CLIENT_SECRET)
                    + "&redirect_uri=" + urlEncode(REDIRECT_URI)
                    + "&grant_type=authorization_code";

            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.flush();

            if (conn.getResponseCode() == 200) {
                return readStream(conn.getInputStream());
            } else {
                // Log error response
                System.err.println("Google token error: " + readStream(conn.getErrorStream()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUserInfo(String accessToken) {
        try {
            URL url = new URL(GOOGLE_USER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            if (conn.getResponseCode() == 200) {
                return readStream(conn.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ---------------------------------------------------------------
    // Utility
    // ---------------------------------------------------------------
    private String readStream(InputStream is) throws IOException {
        if (is == null) {
            return "";
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * Hàm trích xuất JSON hỗ trợ khoảng trắng linh hoạt (Sử dụng Regex)
     */
    private String extractJson(String json, String key) {
        // Biểu thức regex tìm format: "key" : "value" (cho phép khoảng trắng ở giữa)
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }
}
