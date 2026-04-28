<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="pageTitle" value="Tạo Khóa Học Mới - Admin"/>
</jsp:include>

<div class="page-title">
    <h2>Tạo Khóa Học Mới</h2>
    <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-secondary">&larr; Quay Lại</a>
</div>

<c:if test="${not empty errorMsg}">
    <div class="alert alert-error">${errorMsg}</div>
</c:if>

<div class="form-box">
<form method="post" action="${pageContext.request.contextPath}/admin/courses">
    <input type="hidden" name="action" value="create"/>

    <div class="form-group">
        <label>Tên Khóa Học <span class="req">*</span></label>
        <input type="text" name="courseName" placeholder="Nhập tên khóa học..." required/>
    </div>

    <div class="form-group">
        <label>URL Hình Ảnh</label>
        <input type="text" name="image" placeholder="https://example.com/image.jpg"/>
    </div>

    <div class="form-group">
        <label>Mô Tả</label>
        <textarea name="description" rows="4" placeholder="Mô tả nội dung khóa học..."></textarea>
    </div>

    <div class="form-row">
        <div class="form-group">
            <label>Học Phí (VNĐ) <span class="req">*</span></label>
            <input type="number" name="tuitionFee" placeholder="0" min="0" step="1000" required/>
        </div>
        <div class="form-group">
            <label>Số Chỗ <span class="req">*</span></label>
            <input type="number" name="quantity" placeholder="0" min="0" required/>
        </div>
    </div>

    <div class="form-row">
        <div class="form-group">
            <label>Ngày Bắt Đầu <span class="req">*</span></label>
            <input type="date" name="startDate" required/>
        </div>
        <div class="form-group">
            <label>Ngày Kết Thúc <span class="req">*</span></label>
            <input type="date" name="endDate" required/>
        </div>
    </div>

    <div class="form-group">
        <label>Danh Mục <span class="req">*</span></label>
        <select name="categoryID" required>
            <option value="">-- Chọn danh mục --</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.categoryID}">${cat.categoryName}</option>
            </c:forEach>
        </select>
    </div>

    <div class="form-group">
        <label>Trạng Thái</label>
        <input type="text" value="active (mặc định)" readonly class="readonly-field"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-success">✅ Tạo Khóa Học</button>
        <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-secondary">Hủy</a>
    </div>
</form>
</div>

<jsp:include page="../common/footer.jsp"/>
