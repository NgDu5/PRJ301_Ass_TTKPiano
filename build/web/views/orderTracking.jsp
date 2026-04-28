<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Theo Dõi Đơn Hàng - TTK Piano"/>
</jsp:include>

<div class="page-title"><h2>🔍 Theo Dõi Đơn Hàng</h2></div>

<!-- Search Form -->
<div class="form-box">
<form method="post" action="${pageContext.request.contextPath}/order-tracking">
    <div class="form-group">
        <label>Nhập Mã Đơn Hàng</label>
        <div style="display:flex;gap:8px">
            <input type="number" name="orderID" value="${searchedOrderID}"
                   placeholder="Ví dụ: 1001" min="1" required style="flex:1"/>
            <button type="submit" class="btn btn-primary">Tìm Kiếm</button>
        </div>
    </div>
</form>
</div>

<!-- Error -->
<c:if test="${not empty errorMsg}">
    <div class="alert alert-error">${errorMsg}</div>
</c:if>

<!-- Order Details -->
<c:if test="${not empty order}">
    <div class="order-detail-box">
        <h3>Chi Tiết Đơn Hàng #${order.orderID}</h3>
        <table class="info-table">
            <tr>
                <th>Khách Hàng</th>
                <td>${order.customerDisplayName}</td>
                <th>Email</th>
                <td>
                    <c:choose>
                        <c:when test="${not empty order.guestEmail}">${order.guestEmail}</c:when>
                        <c:otherwise>--</c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th>Mã Đơn Hàng</th>
                <td>#${order.orderID}</td>
                <th>Ngày Đặt</th>
                <td><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
            </tr>
            <tr>
                <th>Phương Thức TT</th>
                <td>${order.paymentMethod}</td>
                <th>Trạng Thái TT</th>
                <td>
                    <span class="status-badge ${order.paymentStatus}">${order.paymentStatus}</span>
                </td>
            </tr>
            <tr>
                <th>Tổng Tiền</th>
                <td colspan="3" class="price" style="font-size:1.2em">
                    <fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/> VNĐ
                </td>
            </tr>
        </table>

        <h4 style="margin-top:16px">Danh Sách Khóa Học Đã Đăng Ký</h4>
        <div class="table-wrap">
        <table class="data-table">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Tên Khóa Học</th>
                    <th>Số Lượng</th>
                    <th>Học Phí / Chỗ</th>
                    <th>Thành Tiền</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="d" items="${order.orderDetails}" varStatus="st">
                    <tr>
                        <td>${st.index + 1}</td>
                        <td>${d.courseName}</td>
                        <td>${d.quantity}</td>
                        <td><fmt:formatNumber value="${d.tuitionFee}" type="number" groupingUsed="true"/> VNĐ</td>
                        <td><fmt:formatNumber value="${d.total}"      type="number" groupingUsed="true"/> VNĐ</td>
                    </tr>
                </c:forEach>
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="4" style="text-align:right"><strong>Tổng Cộng:</strong></td>
                    <td class="price">
                        <fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/> VNĐ
                    </td>
                </tr>
            </tfoot>
        </table>
        </div>
    </div>
</c:if>

<jsp:include page="/views/common/footer.jsp"/>
