<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Lỗi - TTK Piano"/>
</jsp:include>

<div class="page-title"><h2>⚠️ Có Lỗi Xảy Ra</h2></div>
<div class="form-box" style="text-align:center">
    <p>Xin lỗi, đã có lỗi xảy ra. Vui lòng thử lại sau.</p>
    <% if (exception != null) { %>
        <p style="color:#999;font-size:0.85em"><%= exception.getMessage() %></p>
    <% } %>
    <a href="${pageContext.request.contextPath}/courses" class="btn btn-primary">Về Trang Chủ</a>
</div>

<jsp:include page="/views/common/footer.jsp"/>
