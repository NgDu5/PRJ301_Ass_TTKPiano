<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Quản Lý Khóa Học - Admin"/>
</jsp:include>

<div class="page-title">
    <h2>Quản Lý Khóa Học</h2>
    <a href="${pageContext.request.contextPath}/admin/courses?action=create" class="btn btn-success">+ Tạo Khóa Học Mới</a>
</div>

<c:if test="${not empty param.success}">
    <div class="alert alert-success">${param.success}</div>
</c:if>
<c:if test="${not empty param.error}">
    <div class="alert alert-error">${param.error}</div>
</c:if>

<!-- Filter Form -->
<form method="get" action="${pageContext.request.contextPath}/admin/courses" class="search-form">
    <input type="text" name="keyword" value="${keyword}" placeholder="Tìm theo tên..."/>
    <select name="categoryID">
        <option value="0">-- Tất cả danh mục --</option>
        <c:forEach var="cat" items="${categories}">
            <option value="${cat.categoryID}"
                <c:if test="${cat.categoryID == categoryID}">selected</c:if>>
                ${cat.categoryName}
            </option>
        </c:forEach>
    </select>
    <select name="statusFilter">
        <option value="">-- Tất cả trạng thái --</option>
        <option value="active"   <c:if test="${statusFilter == 'active'}">selected</c:if>>Active</option>
        <option value="inactive" <c:if test="${statusFilter == 'inactive'}">selected</c:if>>Inactive</option>
    </select>
    <button type="submit" class="btn btn-primary">Tìm</button>
    <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-secondary">Reset</a>
</form>

<p>Tổng: <strong>${total}</strong> khóa học | Trang ${page}/${totalPages}</p>

<!-- Table -->
<div class="table-wrap">
<table class="data-table">
    <thead>
        <tr>
            <th>ID</th>
            <th>Tên Khóa Học</th>
            <th>Danh Mục</th>
            <th>Học Phí (VNĐ)</th>
            <th>Bắt Đầu</th>
            <th>Kết Thúc</th>
            <th>Trạng Thái</th>
            <th>Còn Chỗ</th>
            <th>Cập Nhật Cuối</th>
            <th>Hành Động</th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${empty courses}">
                <tr><td colspan="10" style="text-align:center">Không có dữ liệu</td></tr>
            </c:when>
            <c:otherwise>
                <c:forEach var="c" items="${courses}">
                    <tr>
                        <td>${c.courseID}</td>
                        <td>${c.courseName}</td>
                        <td>${c.categoryName}</td>
                        <td><fmt:formatNumber value="${c.tuitionFee}" type="number" groupingUsed="true"/></td>
                        <td><fmt:formatDate value="${c.startDate}" pattern="dd/MM/yyyy"/></td>
                        <td><fmt:formatDate value="${c.endDate}"   pattern="dd/MM/yyyy"/></td>
                        <td>
                            <span class="status-badge ${c.status}">${c.status}</span>
                        </td>
                        <td>${c.quantity}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty c.lastUpdateDate}">
                                    <fmt:formatDate value="${c.lastUpdateDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    <br/><small>by ${c.lastUpdateUser}</small>
                                </c:when>
                                <c:otherwise>--</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/courses?action=edit&id=${c.courseID}"
                               class="btn btn-warning btn-sm">Sửa</a>
                        </td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </tbody>
</table>
</div>

<!-- Paging -->
<c:if test="${totalPages > 1}">
    <div class="paging">
        <c:if test="${page > 1}">
            <a href="${pageContext.request.contextPath}/admin/courses?page=${page-1}&keyword=${keyword}&categoryID=${categoryID}&statusFilter=${statusFilter}"
               class="btn btn-secondary">&laquo; Trước</a>
        </c:if>
        <c:forEach begin="1" end="${totalPages}" var="p">
            <c:choose>
                <c:when test="${p == page}"><span class="btn btn-primary">${p}</span></c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/admin/courses?page=${p}&keyword=${keyword}&categoryID=${categoryID}&statusFilter=${statusFilter}"
                       class="btn btn-secondary">${p}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <c:if test="${page < totalPages}">
            <a href="${pageContext.request.contextPath}/admin/courses?page=${page+1}&keyword=${keyword}&categoryID=${categoryID}&statusFilter=${statusFilter}"
               class="btn btn-secondary">Tiếp &raquo;</a>
        </c:if>
    </div>
</c:if>

<jsp:include page="/views/common/footer.jsp"/>
