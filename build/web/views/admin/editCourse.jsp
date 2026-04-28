<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Sửa Khóa Học - Admin"/>
</jsp:include>

<div class="page-title">
    <h2>Cập Nhật Khóa Học #${course.courseID}</h2>
    <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-secondary">&larr; Quay Lại</a>
</div>

<c:if test="${not empty errorMsg}">
    <div class="alert alert-error">${errorMsg}</div>
</c:if>

<div class="form-box">
<form method="post" action="${pageContext.request.contextPath}/admin/courses">
    <input type="hidden" name="action"   value="update"/>
    <input type="hidden" name="courseID" value="${course.courseID}"/>

    <div class="form-group">
        <label>Tên Khóa Học <span class="req">*</span></label>
        <input type="text" name="courseName" value="${course.courseName}" required/>
    </div>

    <div class="form-group">
        <label>URL Hình Ảnh</label>
        <input type="text" name="image" value="${course.image}" placeholder="https://..."/>
        <c:if test="${not empty course.image}">
            <br/><img src="${course.image}" alt="preview" class="img-preview"/>
        </c:if>
    </div>

    <div class="form-group">
        <label>Mô Tả</label>
        <textarea name="description" rows="4">${course.description}</textarea>
    </div>

    <div class="form-row">
        <div class="form-group">
            <label>Học Phí (VNĐ) <span class="req">*</span></label>
            <input type="number" name="tuitionFee" value="${course.tuitionFee}" min="0" step="1000" required/>
        </div>
        <div class="form-group">
            <label>Số Chỗ Còn Lại <span class="req">*</span></label>
            <input type="number" name="quantity" value="${course.quantity}" min="0" required/>
        </div>
    </div>

    <div class="form-row">
        <div class="form-group">
            <label>Ngày Bắt Đầu <span class="req">*</span></label>
            <input type="date" name="startDate" value="${course.startDate}" required/>
        </div>
        <div class="form-group">
            <label>Ngày Kết Thúc <span class="req">*</span></label>
            <input type="date" name="endDate" value="${course.endDate}" required/>
        </div>
    </div>

    <div class="form-row">
        <div class="form-group">
            <label>Danh Mục <span class="req">*</span></label>
            <select name="categoryID" required>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat.categoryID}"
                        <c:if test="${cat.categoryID == course.categoryID}">selected</c:if>>
                        ${cat.categoryName}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>Trạng Thái <span class="req">*</span></label>
            <select name="status" required>
                <option value="active"   <c:if test="${course.status == 'active'}">selected</c:if>>Active</option>
                <option value="inactive" <c:if test="${course.status == 'inactive'}">selected</c:if>>Inactive</option>
            </select>
        </div>
    </div>

    <div class="form-group">
        <label>Ngày Tạo</label>
        <input type="text" value="<fmt:formatDate value='${course.createDate}' pattern='dd/MM/yyyy HH:mm'/>" readonly class="readonly-field"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary">💾 Lưu Thay Đổi</button>
        <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-secondary">Hủy</a>
    </div>
</form>
</div>

<jsp:include page="/views/common/footer.jsp"/>
