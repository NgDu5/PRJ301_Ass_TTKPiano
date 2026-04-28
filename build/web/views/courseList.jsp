<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Danh Sách Khóa Học - TTK Piano"/>
</jsp:include>

<div class="page-title">
    <h2>Danh Sách Khóa Học</h2>
    <p>Tìm thấy <strong>${total}</strong> khóa học</p>
</div>

<!-- Search Form -->
<form method="get" action="${pageContext.request.contextPath}/courses" class="search-form">
    <input type="text" name="keyword" value="${keyword}" placeholder="Tìm theo tên khóa học..."/>
    <select name="categoryID">
        <option value="0">-- Tất cả danh mục --</option>
        <c:forEach var="cat" items="${categories}">
            <option value="${cat.categoryID}"
                    <c:if test="${cat.categoryID == categoryID}">selected</c:if>>
                ${cat.categoryName}
            </option>
        </c:forEach>
    </select>
    <button type="submit" class="btn btn-primary">Tìm Kiếm</button>
    <a href="${pageContext.request.contextPath}/courses" class="btn btn-secondary">Xóa Lọc</a>
</form>

<c:if test="${not empty param.error}">
    <div class="alert alert-error">${param.error}</div>
</c:if>

<!-- Course Grid -->
<c:choose>
    <c:when test="${empty courses}">
        <div class="alert alert-info">Không có khóa học nào phù hợp.</div>
    </c:when>
    <c:otherwise>
        <div class="course-grid">
            <c:forEach var="course" items="${courses}">
                <div class="course-card">
                    <c:choose>
                        <c:when test="${not empty course.image}">
                            <img src="${course.image}" alt="${course.courseName}" class="course-img"/>
                        </c:when>
                        <c:otherwise>
                            <div class="course-img-placeholder">🎵</div>
                        </c:otherwise>
                    </c:choose>
                    <div class="course-info">
                        <span class="badge-cat">${course.categoryName}</span>
                        <h3>${course.courseName}</h3>
                        <p class="desc">${course.description}</p>
                        <p>
                            <strong>Học phí:</strong>
                            <span class="price">
                                <fmt:formatNumber value="${course.tuitionFee}" type="number" groupingUsed="true"/> VNĐ
                            </span>
                        </p>
                        <p>
                            <strong>Bắt đầu:</strong>
                            <fmt:formatDate value="${course.startDate}" pattern="dd/MM/yyyy"/>
                            &nbsp;-&nbsp;
                            <strong>Kết thúc:</strong>
                            <fmt:formatDate value="${course.endDate}" pattern="dd/MM/yyyy"/>
                        </p>
                        <p><strong>Còn lại:</strong> ${course.quantity} chỗ</p>
                        <form method="post" action="${pageContext.request.contextPath}/cart">
                            <input type="hidden" name="action"   value="add"/>
                            <input type="hidden" name="courseID" value="${course.courseID}"/>
                            <c:set var="userLogged" value="${sessionScope.loggedUser}"/>

                            <c:if test="${!userLogged.admin == true}">
                                <button type="submit" class="btn btn-primary">🛒 Đăng Ký</button>
                            </c:if>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<!-- Paging -->
<c:if test="${totalPages > 1}">
    <div class="paging">
        <c:if test="${page > 1}">
            <a href="${pageContext.request.contextPath}/courses?page=${page-1}&keyword=${keyword}&categoryID=${categoryID}"
               class="btn btn-secondary">&laquo; Trước</a>
        </c:if>

        <c:forEach begin="1" end="${totalPages}" var="p">
            <c:choose>
                <c:when test="${p == page}">
                    <span class="btn btn-primary">${p}</span>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/courses?page=${p}&keyword=${keyword}&categoryID=${categoryID}"
                       class="btn btn-secondary">${p}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${page < totalPages}">
            <a href="${pageContext.request.contextPath}/courses?page=${page+1}&keyword=${keyword}&categoryID=${categoryID}"
               class="btn btn-secondary">Tiếp &raquo;</a>
        </c:if>
        <span style="margin-left:10px">Trang ${page} / ${totalPages}</span>
    </div>
</c:if>

<jsp:include page="/views/common/footer.jsp"/>
