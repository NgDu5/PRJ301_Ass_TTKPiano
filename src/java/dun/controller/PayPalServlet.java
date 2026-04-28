package dun.controller;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.*;
import model.DAO.OrderDAO;
import model.DTO.CartItem;
import model.DTO.Order;
import model.DTO.User;

/**
 * Function 7: PayPal Online Payment (Sandbox)
 *
 * Flow: 1. User clicks "Pay with PayPal" in cart → POST /paypal?action=pay 2.
 * Servlet creates Order (pending), builds PayPal checkout URL, redirects user
 * 3. User approves on PayPal → PayPal redirects to
 * /paypal?action=success&orderID=xxx 4. Servlet marks order as paid, clears
 * cart, shows success 5. User cancels → /paypal?action=cancel
 *
 * ⚠️ SETUP: Fill in your PayPal Sandbox credentials below. Create sandbox
 * account at https://developer.paypal.com
 */
@WebServlet(name = "PayPalServlet", urlPatterns = {"/paypal"})
public class PayPalServlet extends HttpServlet {

    // ---------------------------------------------------------------
    // TODO: Replace with your PayPal Sandbox credentials
    // ---------------------------------------------------------------
    private static final String PAYPAL_CLIENT_ID = "";
    private static final String PAYPAL_CLIENT_SECRET = "";
    
    private static final String PAYPAL_API_BASE = "https://sandbox.paypal.com";

    
    private static final double VND_TO_USD = 25000.0;

    // ---------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if ("success".equals(action)) {
            handleSuccess(req, resp);
        } else if ("cancel".equals(action)) {
            handleCancel(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("pay".equals(action)) {
            handlePay(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    // ---------------------------------------------------------------
    // Step 1: Create order in DB (paymentStatus=pending), then redirect to PayPal
    // ---------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private void handlePay(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Map<Integer, CartItem> cart = (session != null)
                ? (Map<Integer, CartItem>) session.getAttribute("cart") : null;

        if (cart == null || cart.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=Gio+hang+trong.");
            return;
        }

        User loggedUser = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        // Build order object
        Order order = new Order();
        if (loggedUser != null) {
            order.setUserID(loggedUser.getUserID());
        } else {
            String guestName = req.getParameter("guestName");
            String guestEmail = req.getParameter("guestEmail");
            String guestPhone = req.getParameter("guestPhone");
            if (guestName == null || guestName.trim().isEmpty()
                    || guestEmail == null || guestEmail.trim().isEmpty()
                    || guestPhone == null || guestPhone.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/cart?error=Vui+long+nhap+thong+tin.");
                return;
            }
            order.setGuestName(guestName.trim());
            order.setGuestEmail(guestEmail.trim());
            order.setGuestPhone(guestPhone.trim());
        }

        double totalVND = cart.values().stream().mapToDouble(CartItem::getTotal).sum();
        order.setPaymentMethod("paypal");
        order.setPaymentStatus("pending");
        order.setTotalAmount(totalVND);

        // Save order to DB with paymentStatus = pending
        OrderDAO orderDAO = new OrderDAO();
        List<CartItem> items = new ArrayList<>(cart.values());
        int orderID = orderDAO.createOrder(order, items);

        if (orderID <= 0) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=Dat+hang+that+bai.");
            return;
        }

        // Store orderID in session for callback
        session.setAttribute("pendingPayPalOrderID", orderID);

        // Get PayPal access token
        String accessToken = getPayPalAccessToken();
        if (accessToken == null) {
            req.setAttribute("errorMsg", "Không thể kết nối PayPal. Vui lòng thử lại.");
            resp.sendRedirect(req.getContextPath() + "/cart?error=Khong+the+ket+noi+PayPal.");
            return;
        }

        // Build return/cancel URLs
        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()
                + req.getContextPath();
        String returnUrl = baseUrl + "/paypal?action=success&orderID=" + orderID;
        String cancelUrl = baseUrl + "/paypal?action=cancel&orderID=" + orderID;

        // Convert VND to USD (PayPal requires USD for sandbox)
        double totalUSD = Math.max(0.01, totalVND / VND_TO_USD);
        String amountStr = String.format(java.util.Locale.US, "%.2f", totalUSD);

        // Create PayPal order via REST API
        String paypalOrderId = createPayPalOrder(accessToken, amountStr, returnUrl, cancelUrl);
        if (paypalOrderId == null) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=Loi+tao+don+PayPal.");
            return;
        }

        // Redirect to PayPal approval URL
        String approvalUrl = PAYPAL_API_BASE + "/checkoutnow?token=" + paypalOrderId;
        resp.sendRedirect(approvalUrl);
    }

    // ---------------------------------------------------------------
    // Step 2: PayPal callback - capture payment & update DB
    // ---------------------------------------------------------------
    private void handleSuccess(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = req.getParameter("token");   // PayPal order token
        String orderIDStr = req.getParameter("orderID");

        if (token == null || orderIDStr == null) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        int orderID;
        try {
            orderID = Integer.parseInt(orderIDStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        // Capture payment on PayPal
        String accessToken = getPayPalAccessToken();
        boolean captured = (accessToken != null) && capturePayPalOrder(accessToken, token);

        OrderDAO orderDAO = new OrderDAO();
        if (captured) {
            // Mark order as paid in DB
            orderDAO.updatePaymentStatus(orderID, "paid");

            // Clear cart
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute("cart");
                session.removeAttribute("pendingPayPalOrderID");
            }

            req.setAttribute("orderID", orderID);
            req.setAttribute("paypalSuccess", true);
            req.getRequestDispatcher("views/orderSuccess.jsp").forward(req, resp);
        } else {
            // Payment capture failed → mark as failed
            orderDAO.updatePaymentStatus(orderID, "failed");
            resp.sendRedirect(req.getContextPath() + "/cart?error=Thanh+toan+PayPal+that+bai.");
        }
    }

    private void handleCancel(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String orderIDStr = req.getParameter("orderID");
        if (orderIDStr != null) {
            try {
                int orderID = Integer.parseInt(orderIDStr);
                new OrderDAO().updatePaymentStatus(orderID, "cancelled");
            } catch (NumberFormatException ignored) {
            }
        }
        resp.sendRedirect(req.getContextPath() + "/cart?error=Ban+da+huy+thanh+toan+PayPal.");
    }

    // ---------------------------------------------------------------
    // PayPal REST API Helpers
    // ---------------------------------------------------------------
    /**
     * Get OAuth2 access token from PayPal
     */
    private String getPayPalAccessToken() {
        try {
            URL url = new URL(PAYPAL_API_BASE + "/v1/oauth2/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Basic auth: clientId:secret in Base64
            String credentials = PAYPAL_CLIENT_ID + ":" + PAYPAL_CLIENT_SECRET;
            String encoded = Base64.getEncoder().encodeToString(credentials.getBytes("UTF-8"));
            conn.setRequestProperty("Authorization", "Basic " + encoded);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            os.write("grant_type=client_credentials".getBytes("UTF-8"));
            os.flush();

            if (conn.getResponseCode() == 200) {
                String json = readStream(conn.getInputStream());
                // Simple parse: find "access_token":"..."
                return extractJson(json, "access_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a PayPal order and return the PayPal order ID (token)
     */
    private String createPayPalOrder(String accessToken, String amount,
            String returnUrl, String cancelUrl) {
        try {
            URL url = new URL(PAYPAL_API_BASE + "/v2/checkout/orders");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");

            String body = "{"
                    + "\"intent\":\"CAPTURE\","
                    + "\"purchase_units\":[{\"amount\":{\"currency_code\":\"USD\",\"value\":\"" + amount + "\"}}],"
                    + "\"application_context\":{"
                    + "\"return_url\":\"" + returnUrl + "\","
                    + "\"cancel_url\":\"" + cancelUrl + "\""
                    + "}}";

            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.flush();

            if (conn.getResponseCode() == 201) {
                String json = readStream(conn.getInputStream());
                return extractJson(json, "id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Capture (finalize) an approved PayPal order
     */
    private boolean capturePayPalOrder(String accessToken, String paypalOrderId) {
        try {
            URL url = new URL(PAYPAL_API_BASE + "/v2/checkout/orders/" + paypalOrderId + "/capture");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.getOutputStream().write("{}".getBytes("UTF-8"));

            int code = conn.getResponseCode();
            return (code == 200 || code == 201);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------------------------------------------------------------
    // Utility
    // ---------------------------------------------------------------
    private String readStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * * Hàm trích xuất JSON hỗ trợ khoảng trắng linh hoạt (Sử dụng Regex)
     */
    private String extractJson(String json, String key) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        java.util.regex.Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
