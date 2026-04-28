<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Đặt Hàng Thành Công - TTK Piano"/>
</jsp:include>

<div class="page-title"><h2>✅ Đặt Hàng Thành Công!</h2></div>

<div class="form-box" style="text-align:center">
    <div style="font-size:4em">🎉</div>
    <h3>Cảm ơn bạn đã đăng ký khóa học!</h3>
    <p>Đơn hàng của bạn đã được ghi nhận thành công.</p>
    <p>
        Mã đơn hàng của bạn là:
        <strong style="font-size:1.5em; color:#2196F3;">#${orderID}</strong>
    </p>
    <p>Vui lòng lưu mã đơn hàng để theo dõi trạng thái.</p>
    <hr/>
    <p>
        <a href="${pageContext.request.contextPath}/order-tracking" class="btn btn-primary">
            🔍 Theo Dõi Đơn Hàng
        </a>
        <a href="${pageContext.request.contextPath}/courses" class="btn btn-secondary">
            Tiếp Tục Xem Khóa Học
        </a>
    </p>
</div>

<jsp:include page="/views/common/footer.jsp"/>
