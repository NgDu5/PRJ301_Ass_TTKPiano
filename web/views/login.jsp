<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Đăng Nhập - TTK Piano"/>
</jsp:include>

<div class="page-title"><h2>Đăng Nhập</h2></div>

<div class="form-box">
    <c:if test="${not empty errorMsg}">
        <div class="alert alert-error">${errorMsg}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <c:if test="${not empty param.redirect}">
            <input type="hidden" name="redirect" value="${param.redirect}"/>
        </c:if>

        <div class="form-group">
            <label>Tên đăng nhập</label>
            <input type="text" name="userID" value="${inputUserID}" placeholder="Nhập userID..." required autofocus/>
            <!--<input type="text" name="userID" value="admin" placeholder="Nhập userID..." required autofocus/>-->
        </div>
        <div class="form-group">
            <label>Mật khẩu</label>
            <input type="password" name="password" placeholder="Nhập mật khẩu..." required/>
            <!--<input type="password" name="password" value="123" placeholder="Nhập mật khẩu..." required/>-->
        </div>
        <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block">Đăng Nhập</button>
        </div>
    </form>

    <hr/>
    <div style="text-align:center; margin-top:10px;">
        <p style="color:#666; font-size:0.9em; margin-bottom:8px;">— hoặc —</p>
        <a href="${pageContext.request.contextPath}/google-login?action=redirect"
           class="btn"
           style="background:#fff; color:#444; border:1px solid #ddd;
           display:inline-flex; align-items:center; gap:8px;
           padding:8px 20px; font-size:0.95em;">
            <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                 alt="Google" style="width:20px; height:20px;"/>
            Đăng nhập bằng Google
        </a>
    </div>
    <p style="text-align:center; font-size:0.9em; color:#666;">
        <!--        Tài khoản demo: <strong>admin / 123</strong> &nbsp;|&nbsp;
                <strong>customer1 / 123</strong>-->
    </p>
</div>

<jsp:include page="/views/common/footer.jsp"/>
