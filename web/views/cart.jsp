<%@page import="model.DTO.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Giỏ Hàng - TTK Piano"/>
</jsp:include>

<div class="page-title"><h2>🛒 Giỏ Hàng</h2></div>

<c:if test="${not empty param.error}">
    <div class="alert alert-error">${param.error}</div>
</c:if>
<c:if test="${not empty errorMsg}">
    <div class="alert alert-error">${errorMsg}</div>
</c:if>

<c:choose>
    <c:when test="${empty cart}">
        <div class="alert alert-info">
            Giỏ hàng trống. <a href="${pageContext.request.contextPath}/courses">Xem danh sách khóa học</a>
        </div>
    </c:when>
    <c:otherwise>
        <!-- Cart Table -->
        <div class="table-wrap">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Hình</th>
                        <th>Tên Khóa Học</th>
                        <th>Học Phí / Chỗ</th>
                        <th>Số Lượng</th>
                        <th>Thành Tiền</th>
                        <th>Xóa</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="entry" items="${cart}">
                        <c:set var="item" value="${entry.value}"/>
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty item.image}">
                                        <img src="${item.image}" alt="${item.courseName}" style="width:80px;height:55px;object-fit:cover;border-radius:4px;"/>
                                    </c:when>
                                    <c:otherwise>🎵</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${item.courseName}</td>
                            <td><fmt:formatNumber value="${item.tuitionFee}" type="number" groupingUsed="true"/> VNĐ</td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/cart" style="display:inline">
                                    <input type="hidden" name="action"   value="update"/>
                                    <input type="hidden" name="courseID" value="${item.courseID}"/>
                                    <input type="number" name="quantity" value="${item.quantity}" min="1" max="99"
                                           style="width:60px" onchange="this.form.submit()"/>
                                </form>
                            </td>
                            <td><fmt:formatNumber value="${item.total}" type="number" groupingUsed="true"/> VNĐ</td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/cart"
                                      onsubmit="return confirm('Xóa khóa học này khỏi giỏ hàng?')">
                                    <input type="hidden" name="action"   value="remove"/>
                                    <input type="hidden" name="courseID" value="${item.courseID}"/>
                                    <button type="submit" class="btn btn-danger btn-sm">Xóa</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="4" style="text-align:right"><strong>Tổng Cộng:</strong></td>
                        <td colspan="2" class="price" style="font-size:1.2em">
                            <fmt:formatNumber value="${total}" type="number" groupingUsed="true"/> VNĐ
                        </td>
                    </tr>
                </tfoot>
            </table>
        </div>

        <!-- Confirm Order -->
        <%
            User loggedUser
                    = (User) session.getAttribute("loggedUser");
            boolean showGuestForm = (loggedUser == null);
            Boolean showGuestFormAttr = (Boolean) request.getAttribute("showGuestForm");
            if (showGuestFormAttr != null)
                showGuestForm = showGuestFormAttr;
        %>

        <div class="cart-confirm">
            <form method="post" action="${pageContext.request.contextPath}/cart"
                  onsubmit="return confirmOrder()">
                <input type="hidden" name="action" value="confirm"/>

                <% if (showGuestForm) { %>
                <div class="guest-form">
                    <h3>Thông Tin Đặt Hàng</h3>
                    <p><em>Bạn chưa đăng nhập. Vui lòng nhập thông tin để đặt hàng.</em></p>
                    <div class="form-row">
                        <div class="form-group">
                            <label>Họ và Tên <span class="req">*</span></label>
                            <input type="text" name="guestName" placeholder="Nhập họ tên..." required/>
                        </div>
                        <div class="form-group">
                            <label>Email <span class="req">*</span></label>
                            <input type="email" name="guestEmail" placeholder="email@example.com" required/>
                        </div>
                        <div class="form-group">
                            <label>Số Điện Thoại <span class="req">*</span></label>
                            <input type="tel" name="guestPhone" placeholder="09xxxxxxxx" required/>
                        </div>
                    </div>
                </div>
                <% } else {%>
                <p>Đặt hàng với tài khoản: <strong><%= loggedUser.getFullName()%></strong></p>
                <% } %>

                <p><strong>Chọn phương thức thanh toán:</strong></p>

                <!-- === Nút 1: Tiền mặt (giữ nguyên form cũ) === -->
                <button type="submit" class="btn btn-success" style="font-size:1.1em">
                    ✅ Thanh Toán Tiền Mặt (Cash)
                </button>

              </form> &nbsp;&nbsp;

                <!-- === Nút 2: PayPal (form riêng POST /paypal) === -->
                <form method="post" action="${pageContext.request.contextPath}/paypal"
                      style="display:inline"
                      onsubmit="return confirm('Chuyển sang PayPal để thanh toán?')">
                    <input type="hidden" name="action" value="pay"/>

                    <%-- Nếu là guest, truyền thêm thông tin --%>
                    <% if (showGuestForm) { %>
                    <%-- Các field guest được lấy từ form cha, cần dùng JS để copy --%>
                    <input type="hidden" name="guestName"  id="pp_guestName"/>
                    <input type="hidden" name="guestEmail" id="pp_guestEmail"/>
                    <input type="hidden" name="guestPhone" id="pp_guestPhone"/>
                    <% }%>

                    <button type="submit" class="btn" onclick="copyGuestToPayPal()"
                            style="background:#003087;color:#fff;font-size:1.1em;padding:8px 18px;">
                        <img src="https://www.paypalobjects.com/webstatic/mktg/Logo/pp-logo-100px.png"
                             alt="PayPal" style="height:20px;vertical-align:middle;margin-right:6px;"/>
                        Thanh Toán PayPal
                    </button>
                </form>

                <!-- Script copy guest info sang form PayPal -->
                <script>
                    function copyGuestToPayPal() {
                        var n = document.querySelector('input[name="guestName"]');
                        var e = document.querySelector('input[name="guestEmail"]');
                        var p = document.querySelector('input[name="guestPhone"]');
                        if (n)
                            document.getElementById('pp_guestName').value = n.value;
                        if (e)
                            document.getElementById('pp_guestEmail').value = e.value;
                        if (p)
                            document.getElementById('pp_guestPhone').value = p.value;
                        return true;
                    }
                </script>
                <a href="${pageContext.request.contextPath}/courses" class="btn btn-secondary">Tiếp Tục Mua</a>
            </form>
        </div>
    </c:otherwise>
</c:choose>

<script>
    function confirmOrder() {
        return confirm('Xác nhận đặt hàng? Tổng tiền: ${total} VNĐ');
    }
</script>

<jsp:include page="/views/common/footer.jsp"/>
